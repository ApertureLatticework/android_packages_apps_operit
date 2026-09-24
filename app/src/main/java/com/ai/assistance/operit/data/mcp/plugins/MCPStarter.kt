package com.ai.assistance.operit.data.mcp.plugins

import android.content.Context
import com.ai.assistance.operit.util.AppLogger
import com.ai.assistance.operit.core.tools.mcp.MCPManager
import com.ai.assistance.operit.core.tools.mcp.McpRuntimeDescriptor
import com.ai.assistance.operit.data.mcp.MCPLocalServer
import com.ai.assistance.operit.data.mcp.MCPRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * MCP Plugin Starter
 *
 * 本地 bridge/stdio 插件链路随 terminal 线裁撤，仅协调 Kotlin SDK 远程插件。
 */
class MCPStarter(private val context: Context) {
    companion object {
        private const val TAG = "MCPStarter"
    }

    // Coroutine scope for async operations
    private val starterDispatcher = Dispatchers.IO.limitedParallelism(6)
    private val starterScope = CoroutineScope(starterDispatcher + SupervisorJob())

    /** Plugin initialization status enum */
    enum class PluginInitStatus {
        SUCCESS,
        OTHER_ERROR
    }

    /** Plugin start progress listener interface */
    interface PluginStartProgressListener {
        fun onPluginStarting(pluginId: String, index: Int, total: Int) {}
        fun onPluginRegistered(pluginId: String, serviceName: String, success: Boolean) {}
        fun onPluginStarted(pluginId: String, success: Boolean, index: Int, total: Int) {}
        fun onPluginLog(pluginId: String, message: String) {}
        fun onAllPluginsStarted(
            successCount: Int,
            totalCount: Int,
            status: PluginInitStatus = PluginInitStatus.SUCCESS
        ) {
        }

        fun onAllPluginsVerified(verificationResults: List<VerificationResult>) {}
    }

    /** Start a remote plugin */
    suspend fun startPlugin(pluginId: String, statusCallback: (StartStatus) -> Unit): Boolean {
        return startPluginInternal(pluginId, statusCallback)
    }

    /** Internal plugin start logic */
    private suspend fun startPluginInternal(
        pluginId: String,
        statusCallback: (StartStatus) -> Unit
    ): Boolean {
        try {
            val mcpRepository = MCPRepository(context)
            AppLogger.d(TAG, "Refreshing MCP config before starting plugin: $pluginId")
            mcpRepository.refreshPluginList()

            val pluginInfo = mcpRepository.getInstalledPluginInfo(pluginId)
            if (pluginInfo == null) {
                statusCallback(StartStatus.Error("Plugin info not found: $pluginId"))
                return false
            }

            val mcpLocalServer = MCPLocalServer.getInstance(context)

            // Check if plugin is enabled by the user
            val isEnabled = mcpLocalServer.isServerEnabled(pluginId)
            if (!isEnabled) {
                statusCallback(StartStatus.Error("Plugin not enabled by user: $pluginId"))
                return false
            }

            statusCallback(StartStatus.InProgress("Starting plugin: $pluginId"))

            val mcpManager = MCPManager.getInstance(context)
            val descriptor = McpRuntimeDescriptor.Remote(
                endpoint = requireNotNull(pluginInfo.endpoint) {
                    "Remote service is missing endpoint: $pluginId"
                },
                connectionType = requireNotNull(pluginInfo.connectionType) {
                    "Remote service is missing connection type: $pluginId"
                },
                bearerToken = pluginInfo.bearerToken,
                headers = pluginInfo.headers.orEmpty()
            )
            mcpManager.registerRuntime(pluginId, descriptor)

            val session = mcpManager.getOrCreateSession(pluginId)
            if (session == null) {
                statusCallback(StartStatus.Error("Failed to connect to remote MCP service: $pluginId"))
                return false
            }

            statusCallback(StartStatus.Success("Remote service $pluginId connected successfully"))
            return true
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error starting plugin", e)
            statusCallback(StartStatus.Error("Start error: ${e.message}"))
            return false
        }
    }

    /** Start all enabled remote plugins */
    fun startAllDeployedPlugins(
        progressListener: PluginStartProgressListener = object : PluginStartProgressListener {}
    ) {
        starterScope.launch {
            try {
                val mcpRepository = MCPRepository(context)
                val mcpLocalServer = MCPLocalServer.getInstance(context)
                AppLogger.d(TAG, "Refreshing MCP config before batch startup")
                mcpRepository.refreshPluginList()

                // Get all installed plugins and partition into enabled and disabled
                val allInstalledPlugins = mcpRepository.installedPluginIds.first()
                val (pluginsToStart, disabledPlugins) = allInstalledPlugins.partition { pluginId ->
                    mcpLocalServer.isServerEnabled(pluginId)
                }

                mcpRepository.unregisterToolsForPlugins(disabledPlugins)

                if (pluginsToStart.isEmpty()) {
                    progressListener.onAllPluginsStarted(0, 0, PluginInitStatus.SUCCESS)
                    return@launch
                }

                // 将注册与处理串联到同一个插件任务中，避免“全部先转 loading，再同时完成”的体验
                val allVerificationResults = mutableListOf<VerificationResult>()
                val batchSize = 4
                val semaphore = Semaphore(batchSize)
                var pluginsProcessingStartedCount = 0
                var pluginsProcessedCount = 0
                val totalPluginsToProcess = pluginsToStart.size

                val jobs =
                    pluginsToStart.map { pluginId ->
                        async {
                            val serviceName = registerPlugin(pluginId, progressListener)
                            if (serviceName == null) {
                                progressListener.onPluginRegistered(pluginId, "", false)

                                val currentIndex = synchronized(this@MCPStarter) {
                                    pluginsProcessedCount++
                                    pluginsProcessedCount
                                }

                                progressListener.onPluginStarted(
                                    pluginId,
                                    false,
                                    currentIndex,
                                    totalPluginsToProcess
                                )
                                return@async
                            }

                            progressListener.onPluginRegistered(pluginId, serviceName, true)

                            val result = semaphore.withPermit {
                                val startIndex = synchronized(this@MCPStarter) {
                                    pluginsProcessingStartedCount++
                                    pluginsProcessingStartedCount
                                }

                                progressListener.onPluginStarting(
                                    pluginId,
                                    startIndex,
                                    totalPluginsToProcess
                                )

                                processPlugin(
                                    pluginId,
                                    serviceName,
                                    mcpRepository,
                                    progressListener
                                )
                            }

                            synchronized(allVerificationResults) {
                                allVerificationResults.add(result)
                            }

                            val currentIndex = synchronized(this@MCPStarter) {
                                pluginsProcessedCount++
                                pluginsProcessedCount
                            }

                            progressListener.onPluginStarted(
                                result.pluginId,
                                result.isResponding,
                                currentIndex,
                                totalPluginsToProcess
                            )
                            delay(150)
                        }
                    }

                jobs.awaitAll()

                val successfulResults = allVerificationResults.filter { it.isResponding }
                if (successfulResults.isNotEmpty()) {
                    generateMissingDescriptions(successfulResults)
                }

                val successCount = successfulResults.size
                AppLogger.i(TAG, "All plugin batches processed. Total successful: $successCount")

                progressListener.onAllPluginsStarted(
                    successCount,
                    pluginsToStart.size,
                    PluginInitStatus.SUCCESS
                )

            } catch (e: Exception) {
                AppLogger.e(TAG, "Error starting plugins", e)
                progressListener.onAllPluginsStarted(0, 0, PluginInitStatus.OTHER_ERROR)
            }
        }
    }

    private suspend fun processPlugin(
        pluginId: String,
        serviceName: String,
        mcpRepository: MCPRepository,
        progressListener: PluginStartProgressListener
    ): VerificationResult {
        val mcpLocalServer = MCPLocalServer.getInstance(context)
        val mcpManager = MCPManager.getInstance(context)
        val startTime = System.currentTimeMillis()
        val session = mcpManager.getOrCreateSession(pluginId)
        val responseTime = System.currentTimeMillis() - startTime

        if (session == null || !session.isActive()) {
            val message = mcpManager.getLastConnectionFailureReason(pluginId)
                ?: "Service is not responding"
            progressListener.onPluginLog(pluginId, message)
            return VerificationResult(pluginId, serviceName, false, 0, message)
        }

        if (!mcpLocalServer.hasValidToolCache(pluginId)) {
            cacheToolsFromService(pluginId)
        }

        // Register while this plugin is known to be up. Registering the whole batch after the
        // fan-out means a stalled or failing plugin keeps every other plugin out of the AI tool list.
        mcpRepository.registerToolsForPlugin(pluginId)

        return VerificationResult(
            pluginId = pluginId,
            serviceName = serviceName,
            isResponding = true,
            responseTime = responseTime,
            details = "Service is responding"
        )
    }

    private suspend fun registerPlugin(
        pluginId: String,
        progressListener: PluginStartProgressListener? = null
    ): String? {
        try {
            val mcpRepository = MCPRepository(context)
            val pluginInfo = mcpRepository.getInstalledPluginInfo(pluginId) ?: return null

            val serverName =
                pluginInfo.name.replace(" ", "_").lowercase().ifEmpty {
                    pluginId.split("/").last().lowercase()
                }
            val mcpManager = MCPManager.getInstance(context)

            mcpManager.registerRuntime(
                pluginId,
                McpRuntimeDescriptor.Remote(
                    endpoint = requireNotNull(pluginInfo.endpoint) {
                        "Remote service is missing endpoint: $pluginId"
                    },
                    connectionType = requireNotNull(pluginInfo.connectionType) {
                        "Remote service is missing connection type: $pluginId"
                    },
                    bearerToken = pluginInfo.bearerToken,
                    headers = pluginInfo.headers.orEmpty()
                )
            )
            return serverName
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to register plugin $pluginId", e)
            return null
        }
    }

    /** Verify plugin statuses */
    private fun verifyPlugins(progressListener: PluginStartProgressListener) {
        starterScope.launch {
            try {
                delay(5000) // Wait for services to initialize
                val results = verifyAllMcpPlugins()

                // 自动生成空描述的工具包描述
                generateMissingDescriptions(results)

                // 注册验证成功的插件的工具
                registerToolsForVerifiedPlugins(results)

                progressListener.onAllPluginsVerified(results)
            } catch (e: Exception) {
                AppLogger.e(TAG, "Error verifying plugins", e)
                progressListener.onAllPluginsVerified(emptyList())
            }
        }
    }

    /**
     * 从服务缓存工具列表
     */
    private suspend fun cacheToolsFromService(pluginId: String) {
        try {
            val mcpLocalServer = MCPLocalServer.getInstance(context)
            if (mcpLocalServer.hasValidToolCache(pluginId)) {
                AppLogger.d(TAG, "插件 $pluginId 已有工具缓存，跳过")
                return
            }

            AppLogger.d(TAG, "开始为插件 $pluginId 缓存工具列表")
            val session = MCPManager.getInstance(context).getOrCreateSession(pluginId)
                ?: return
            val tools = session.listTools()

            if (tools.isNotEmpty()) {
                val cachedTools = tools.map { tool ->
                    MCPLocalServer.CachedToolInfo(
                        name = tool.name,
                        description = tool.description,
                        inputSchema = tool.inputSchema,
                        cachedAt = System.currentTimeMillis()
                    )
                }
                mcpLocalServer.cacheServerTools(pluginId, cachedTools)
                AppLogger.i(TAG, "成功缓存插件 $pluginId 的 ${cachedTools.size} 个工具")
            } else {
                AppLogger.w(TAG, "插件 $pluginId 没有返回任何工具")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "缓存插件 $pluginId 的工具列表时出错", e)
        }
    }

    /**
     * 为验证成功的插件注册工具
     * 这确保只有真正就绪并响应的插件才会注册其工具
     */
    private suspend fun registerToolsForVerifiedPlugins(results: List<VerificationResult>) {
        try {
            val mcpRepository = MCPRepository(context)
            val successfulPluginIds = results
                .filter { it.isResponding }
                .map { it.pluginId }

            if (successfulPluginIds.isNotEmpty()) {
                AppLogger.d(
                    TAG,
                    "开始为 ${successfulPluginIds.size} 个验证成功的插件注册工具: $successfulPluginIds"
                )
                mcpRepository.registerToolsForLoadedPlugins(successfulPluginIds)
                AppLogger.d(TAG, "工具注册流程已完成")
            } else {
                AppLogger.d(TAG, "没有验证成功的插件，跳过工具注册")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "注册验证成功插件的工具时出错", e)
        }
    }

    /**
     * 为没有描述的工具包自动生成描述
     */
    private suspend fun generateMissingDescriptions(results: List<VerificationResult>) {
        try {
            val mcpRepository = MCPRepository(context)
            val mcpLocalServer = MCPLocalServer.getInstance(context)

            // 筛选出成功响应的插件
            val respondingPlugins = results.filter { it.isResponding }

            for (result in respondingPlugins) {
                try {
                    // 获取插件信息
                    val pluginInfo = mcpRepository.getInstalledPluginInfo(result.pluginId)

                    // 检查描述是否为空
                    if (pluginInfo != null && pluginInfo.description.isBlank()) {
                        AppLogger.d(TAG, "为插件 ${result.pluginId} 生成描述，当前描述为空")

                        val session = MCPManager.getInstance(context)
                            .getOrCreateSession(result.pluginId)
                            ?: continue
                        val toolDescriptions = session.getToolDescriptions()

                        if (toolDescriptions.isNotEmpty()) {
                            // 调用EnhancedAIService生成描述
                            val generatedDescription =
                                com.ai.assistance.operit.api.chat.EnhancedAIService.generatePackageDescription(
                                    context = context,
                                    pluginName = pluginInfo.name,
                                    toolDescriptions = toolDescriptions
                                )

                            // 只有在AI成功生成时才保存，失败时保持原有的空描述
                            if (generatedDescription.isNotBlank()) {
                                val updatedMetadata =
                                    pluginInfo.copy(description = generatedDescription)
                                mcpLocalServer.addOrUpdatePluginMetadata(updatedMetadata)
                                AppLogger.i(
                                    TAG,
                                    "已为插件 ${result.pluginId} 生成描述: $generatedDescription"
                                )
                            } else {
                                AppLogger.w(TAG, "插件 ${result.pluginId} 的描述生成失败，保持原有空描述")
                            }
                        } else {
                            AppLogger.w(TAG, "插件 ${result.pluginId} 没有可用的工具描述")
                        }
                    }
                } catch (e: Exception) {
                    AppLogger.e(TAG, "为插件 ${result.pluginId} 生成描述时出错: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "生成缺失描述时出错", e)
        }
    }

    /** Verify all MCP plugins */
    suspend fun verifyAllMcpPlugins(): List<VerificationResult> {
        val results = mutableListOf<VerificationResult>()

        try {
            val mcpRepository = MCPRepository(context)
            val mcpLocalServer = MCPLocalServer.getInstance(context)
            val mcpManager = MCPManager.getInstance(context)
            val enabledPlugins = mcpRepository.installedPluginIds.first().filter { pluginId ->
                mcpLocalServer.isServerEnabled(pluginId)
            }

            for (pluginId in enabledPlugins) {
                val pluginInfo = mcpRepository.getInstalledPluginInfo(pluginId) ?: continue
                val serviceName = pluginInfo.name.replace(" ", "_").lowercase()
                    .ifEmpty { pluginId.split("/").last().lowercase() }
                val startTime = System.currentTimeMillis()
                val session = mcpManager.getOrCreateSession(pluginId)
                val isResponding = session?.isActive() == true
                val responseTime = System.currentTimeMillis() - startTime

                results.add(
                    VerificationResult(
                        pluginId = pluginId,
                        serviceName = serviceName,
                        isResponding = isResponding,
                        responseTime = if (isResponding) responseTime else 0,
                        details = if (isResponding) {
                            "Service is responding"
                        } else {
                            mcpManager.getLastConnectionFailureReason(pluginId)
                                ?: "Service is not responding"
                        }
                    )
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error verifying plugins", e)
        }

        return results
    }

    /** Start status */
    sealed class StartStatus {
        object NotStarted : StartStatus()
        data class InProgress(val message: String) : StartStatus()
        data class Success(val message: String) : StartStatus()
        data class Error(val message: String) : StartStatus()
    }

    /** Verification result */
    data class VerificationResult(
        val pluginId: String,
        val serviceName: String,
        val isResponding: Boolean,
        val responseTime: Long,
        val details: String = ""
    )
}
