package com.ai.assistance.operit.data.mcp

import android.content.Context
import com.ai.assistance.operit.R
import com.ai.assistance.operit.api.chat.EnhancedAIService
import com.ai.assistance.operit.util.AppLogger
import com.ai.assistance.operit.core.tools.AIToolHandler
import com.ai.assistance.operit.core.tools.mcp.MCPManager
import com.ai.assistance.operit.core.tools.mcp.MCPPackage
import com.ai.assistance.operit.core.tools.mcp.McpRuntimeDescriptor
import com.ai.assistance.operit.core.tools.mcp.MCPServerConfig
import com.ai.assistance.operit.core.tools.mcp.MCPToolExecutor
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 统一的MCP仓库管理类（remote-only）
 *
 * 职责：
 * - 管理MCP远程服务器的UI状态和数据
 * - 远程服务器的添加、更新、删除
 * - 插件工具的发现与 AI 运行时注册
 *
 * 配置管理由MCPLocalServer单独处理；本地插件的下载/解压/安装链已随
 * terminal 线裁撤整刀删除。
 */
class MCPRepository(private val context: Context) {
    private val mcpLocalServer = MCPLocalServer.getInstance(context)

    companion object {
        private const val TAG = "MCPRepository"
    }

    // UI状态管理
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _mcpServers = MutableStateFlow<List<MCPLocalServer.PluginMetadata>>(emptyList())
    val mcpServers: StateFlow<List<MCPLocalServer.PluginMetadata>> = _mcpServers.asStateFlow()

    // 已配置插件ID管理（远程插件配置后即为已安装）
    private val _installedPluginIds = MutableStateFlow<Set<String>>(emptySet())
    val installedPluginIds: StateFlow<Set<String>> = _installedPluginIds.asStateFlow()

    init {
        loadPluginsFromMCPLocalServer()

        // 监听MCPLocalServer的配置变化
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            mcpLocalServer.pluginMetadata.collect {
                // 当插件元数据发生变化时，重新加载插件列表
                loadPluginsFromMCPLocalServer()
            }
        }
    }

    fun refreshInstalledPlugins() {
        loadPluginsFromMCPLocalServer()
    }

    // ==================== 插件状态管理 ====================

    /**
     * 从MCPLocalServer加载插件信息（主要数据源）
     */
    private fun loadPluginsFromMCPLocalServer() {
        try {
            val pluginMetadata = mcpLocalServer.getAllPluginMetadata()

            // 远程插件配置后即为已安装
            val servers = pluginMetadata.values
                .map { metadata -> metadata.copy(isInstalled = true) }
                .sortedBy { it.name }

            _mcpServers.value = servers
            _installedPluginIds.value = pluginMetadata.keys.toSet()

        } catch (e: Exception) {
            AppLogger.e(TAG, "从MCPLocalServer加载插件失败", e)
        }
    }

    // ==================== 远程服务器管理 ====================

    /**
     * 添加远程服务器
     */
    suspend fun addRemoteServer(server: MCPLocalServer.PluginMetadata) {
        withContext(Dispatchers.IO) {
            if (server.endpoint == null) {
                AppLogger.e(TAG, "addRemoteServer调用了无效的远程服务器: ${server.id}")
                return@withContext
            }

            val metadata = server.copy(
                installedTime = System.currentTimeMillis()
            )

            // Remote servers do not create a local process. The Kotlin MCP runtime reads this metadata.
            mcpLocalServer.addOrUpdatePluginMetadata(metadata)

            // 重新加载插件状态
            loadPluginsFromMCPLocalServer()
        }
    }

    /**
     * 更新远程服务器
     */
    suspend fun updateRemoteServer(server: MCPLocalServer.PluginMetadata) {
        withContext(Dispatchers.IO) {
            val metadata = mcpLocalServer.getPluginMetadata(server.id)
            if (metadata == null) {
                AppLogger.e(TAG, "无法找到要更新的插件元数据: ${server.id}")
                return@withContext
            }

            val updatedMetadata = metadata.copy(
                name = server.name,
                description = server.description,
                longDescription = server.longDescription,
                author = server.author,
                endpoint = server.endpoint,
                connectionType = server.connectionType,
                bearerToken = server.bearerToken,
                headers = server.headers
            )
            mcpLocalServer.addOrUpdatePluginMetadata(updatedMetadata)

            // 重新加载插件状态以更新UI
            loadPluginsFromMCPLocalServer()
        }
    }

    /**
     * 删除远程服务器
     */
    suspend fun removeRemoteServer(serverId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                mcpLocalServer.removeMCPServer(serverId)
                // 重新加载插件状态
                loadPluginsFromMCPLocalServer()
                AppLogger.d(TAG, "远程服务器 $serverId 删除成功")
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "删除远程服务器 $serverId 时出错", e)
                false
            }
        }
    }

    // ==================== 状态同步和管理 ====================

    /**
     * 同步已安装状态
     */
    suspend fun syncInstalledStatus() {
        withContext(Dispatchers.IO) {
            try {
                // 重新从MCPLocalServer加载插件信息
                loadPluginsFromMCPLocalServer()
                AppLogger.d(TAG, "同步插件安装状态完成，${_installedPluginIds.value.size} 个已安装插件")
            } catch (e: Exception) {
                AppLogger.e(TAG, "同步安装状态失败", e)
            }
        }
    }

    /**
     * 初始化仓库
     */
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            // 重新加载插件状态
            loadPluginsFromMCPLocalServer()
        }
    }

    /**
     * 获取已安装插件的信息
     */
    fun getInstalledPluginInfo(pluginId: String): MCPLocalServer.PluginMetadata? {
        return mcpLocalServer.getPluginMetadata(pluginId)
    }

    suspend fun generatePluginDescription(
        pluginId: String,
        pluginName: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val metadata =
                    mcpLocalServer.getPluginMetadata(pluginId)
                        ?: return@withContext Result.failure(
                            IllegalStateException(context.getString(R.string.mcp_repository_server_not_found))
                        )

                val toolDescriptions = collectToolDescriptionsForDescriptionGeneration(metadata)
                if (toolDescriptions.isEmpty()) {
                    return@withContext Result.failure(
                        IllegalStateException(context.getString(R.string.mcp_regenerate_description_no_tools))
                    )
                }

                val targetPluginName = pluginName.trim().ifBlank { metadata.name }
                val generatedDescription =
                    EnhancedAIService.generatePackageDescription(
                        context = context,
                        pluginName = targetPluginName,
                        toolDescriptions = toolDescriptions
                    ).trim()

                if (generatedDescription.isBlank()) {
                    return@withContext Result.failure(
                        IllegalStateException(context.getString(R.string.mcp_regenerate_description_empty))
                    )
                }

                Result.success(generatedDescription)
            } catch (e: Exception) {
                AppLogger.e(TAG, "重新生成插件描述失败: $pluginId", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun collectToolDescriptionsForDescriptionGeneration(
        metadata: MCPLocalServer.PluginMetadata
    ): List<String> {
        val cachedToolDescriptions =
            mcpLocalServer.getCachedTools(metadata.id)
                .orEmpty()
                .mapNotNull { cachedTool ->
                    val toolName = cachedTool.name.trim()
                    if (toolName.isEmpty()) {
                        null
                    } else {
                        cachedTool.description.trim()
                            .takeIf { it.isNotEmpty() }
                            ?.let { "$toolName: $it" }
                            ?: toolName
                    }
                }
        if (cachedToolDescriptions.isNotEmpty()) {
            return cachedToolDescriptions
        }

        val session = MCPManager.getInstance(context).getOrCreateSession(metadata.id)
            ?: return emptyList()
        return session.getToolDescriptions()
    }

    /**
     * Returns the names exposed by a remote MCP service for list presentation, and registers
     * the service with the AI runtime once its tools are known.
     *
     * Remote configuration is persisted independently from the in-memory runtime. This
     * method establishes that runtime boundary from the current metadata before asking
     * the Kotlin SDK session for tools, so a server added while the app is running can
     * be presented immediately.
     */
    suspend fun getRemoteToolNames(pluginId: String): List<String> = withContext(Dispatchers.IO) {
        val metadata = mcpLocalServer.getPluginMetadata(pluginId)
            ?: return@withContext emptyList()

        val toolNames = discoverRemoteToolNames(pluginId, metadata)

        // Discovery is also the moment the server proves it is usable, so hand it to the AI
        // runtime here. Otherwise the management screen lists tools that the AI never sees,
        // because the only other registration trigger runs once during startup.
        if (toolNames.isNotEmpty() && !metadata.disabled) {
            registerToolsForPlugin(pluginId)
        }

        toolNames
    }

    private suspend fun discoverRemoteToolNames(
        pluginId: String,
        metadata: MCPLocalServer.PluginMetadata
    ): List<String> {
        val cachedToolNames = mcpLocalServer.getCachedTools(pluginId)
            .orEmpty()
            .map { cachedTool -> cachedTool.name.trim() }
            .filter { toolName -> toolName.isNotEmpty() }
            .distinct()
        if (cachedToolNames.isNotEmpty()) {
            return cachedToolNames
        }

        if (metadata.disabled) {
            return emptyList()
        }

        val mcpManager = MCPManager.getInstance(context)
        mcpManager.registerRuntime(pluginId, createRuntimeDescriptor(metadata))
        val session = mcpManager.getOrCreateSession(pluginId)
            ?: return emptyList()
        val discoveredTools = session.listTools()
            .mapNotNull { tool ->
                val toolName = tool.name.trim()
                toolName.takeIf { it.isNotEmpty() }?.let { name ->
                    MCPLocalServer.CachedToolInfo(
                        name = name,
                        description = tool.description,
                        inputSchema = tool.inputSchema
                    )
                }
            }

        if (discoveredTools.isNotEmpty()) {
            mcpLocalServer.cacheServerTools(pluginId, discoveredTools)
        }

        return discoveredTools.map { tool -> tool.name }.distinct()
    }

    /**
     * 手动刷新插件列表
     */
    suspend fun refreshPluginList() {
        withContext(Dispatchers.IO) {
            // 重新加载配置文件
            mcpLocalServer.reloadConfigurations()
            // 重新加载插件列表
            loadPluginsFromMCPLocalServer()
            AppLogger.d(TAG, "插件列表已刷新")
        }
    }

    /**
     * 为加载成功的插件注册工具
     * 优先使用本地缓存的工具信息，避免重复连接服务
     *
     * @param successfulPluginIds 加载成功的插件ID列表
     */
    fun registerToolsForLoadedPlugins(successfulPluginIds: List<String>) {
        if (successfulPluginIds.isEmpty()) {
            AppLogger.d(TAG, "没有成功加载的插件，无需注册工具")
            return
        }

        AppLogger.d(TAG, "开始为 ${successfulPluginIds.size} 个插件注册工具: ${successfulPluginIds.joinToString()}")
        successfulPluginIds.forEach { pluginId -> registerToolsForPlugin(pluginId) }
        AppLogger.d(TAG, "所有插件的工具注册流程完成")
    }

    /**
     * 把单个插件接入 AI 运行时：在 MCPManager 中注册服务器（这是 AI 能看到该包的前提），
     * 并把它的工具注册到 AIToolHandler。
     *
     * 注册由多个生命周期节点触发（启动校验、MCP 管理页的远程发现），所以这里必须幂等：
     * 已注册的工具会被跳过，重复调用不会产生重复注册。
     */
    fun registerToolsForPlugin(pluginId: String) {
        try {
            AppLogger.d(TAG, "正在为插件 $pluginId 注册工具...")

            val pluginMetadata = mcpLocalServer.getPluginMetadata(pluginId)
            if (pluginMetadata == null) {
                AppLogger.w(TAG, "在MCPLocalServer中找不到插件 $pluginId 的元数据")
                return
            }

            val mcpManager = MCPManager.getInstance(context)
            val runtimeDescriptor = createRuntimeDescriptor(pluginMetadata)
            val serverConfig = MCPServerConfig(
                name = pluginId,
                endpoint = runtimeDescriptor.endpoint,
                description = pluginMetadata.description,
                capabilities = listOf("tools"),
                extraData = emptyMap()
            )
            mcpManager.registerServer(pluginId, serverConfig, runtimeDescriptor)
            AppLogger.d(TAG, "已在MCPManager中注册服务器: $pluginId")

            // 获取工具信息
            val toolsToRegister = getToolsForPlugin(pluginId)

            if (toolsToRegister.isEmpty()) {
                AppLogger.w(TAG, "插件 $pluginId 没有可注册的工具")
                return
            }

            // 统一注册工具
            val toolHandler = AIToolHandler.getInstance(context)
            val mcpToolExecutor = MCPToolExecutor(context, mcpManager)
            toolsToRegister.forEach { toolInfo ->
                val prefixedToolName = "$pluginId:${toolInfo.name}"

                if (toolHandler.getToolExecutor(prefixedToolName) != null) {
                    AppLogger.d(TAG, "工具 $prefixedToolName 已注册，跳过")
                    return@forEach
                }

                toolHandler.registerTool(
                    name = prefixedToolName,
                    executor = mcpToolExecutor,
                    descriptionGenerator = { tool ->
                        val baseDescription = toolInfo.description
                        val paramsString = if (tool.parameters.isNotEmpty()) {
                            "\nParameters: " + tool.parameters.joinToString(", ") { "${it.name}='${it.value}'" }
                        } else ""
                        baseDescription + paramsString
                    }
                )
                AppLogger.i(TAG, "成功注册工具: $prefixedToolName")
            }
            AppLogger.d(TAG, "插件 $pluginId 的工具注册完成，共 ${toolsToRegister.size} 个")
        } catch (e: Exception) {
            AppLogger.e(TAG, "为插件 $pluginId 注册工具时发生异常", e)
        }
    }

    /**
     * 反注册插件对应的运行时服务器与工具，避免禁用后仍出现在系统提示词。
     */
    fun unregisterToolsForPlugins(pluginIds: List<String>) {
        if (pluginIds.isEmpty()) return

        val mcpManager = MCPManager.getInstance(context)
        val toolHandler = AIToolHandler.getInstance(context)

        pluginIds.forEach { pluginId ->
            try {
                val toolPrefix = "$pluginId:"
                val toolNamesToRemove = toolHandler.getAllToolNames().filter { it.startsWith(toolPrefix) }
                toolNamesToRemove.forEach { toolName ->
                    toolHandler.unregisterTool(toolName)
                }

                mcpManager.unregisterServer(pluginId)

                AppLogger.d(
                    TAG,
                    "Runtime MCP entries removed for $pluginId, tools=${toolNamesToRemove.size}"
                )
            } catch (e: Exception) {
                AppLogger.e(TAG, "Failed to unregister runtime MCP entries for $pluginId", e)
            }
        }
    }

    private fun getToolsForPlugin(pluginId: String): List<UnifiedToolInfo> {
        // 1. 检查缓存
        val cachedTools = mcpLocalServer.getCachedTools(pluginId)
        if (cachedTools != null && cachedTools.isNotEmpty()) {
            AppLogger.d(TAG, "从缓存为插件 $pluginId 获取了 ${cachedTools.size} 个工具")
            return cachedTools.map {
                UnifiedToolInfo(it.name, it.description, it.inputSchema)
            }
        }

        // 2. 如果没有缓存，动态获取
        AppLogger.d(TAG, "插件 $pluginId 无工具缓存，使用动态连接方式获取")
        val mcpManager = MCPManager.getInstance(context)
        val serverConfig = mcpManager.getRegisteredServers()[pluginId]
        if (serverConfig == null) {
            AppLogger.e(TAG, "无法在MCPManager中找到服务器 $pluginId 的配置")
            return emptyList()
        }

        val mcpLoadResult = MCPPackage.loadFromServer(context, serverConfig)
        val mcpPackage = mcpLoadResult.mcpPackage
        if (mcpPackage == null) {
            AppLogger.w(
                TAG,
                "无法从服务器 $pluginId 获取MCP包: ${mcpLoadResult.errorMessage ?: "unknown reason"}"
            )
            return emptyList()
        }

        val toolPackage = mcpPackage.toToolPackage()
        return toolPackage.tools.map {
            val schemaParams = it.parameters.map { param ->
                mapOf(
                    "name" to param.name,
                    "description" to param.description.resolve(context),
                    "type" to param.type,
                    "required" to param.required
                )
            }
            UnifiedToolInfo(
                name = it.name,
                description = it.description.resolve(context),
                inputSchema = Gson().toJson(schemaParams) // 假设 MCPToolExecutor 可以处理
            )
        }
    }

    private fun createRuntimeDescriptor(
        metadata: MCPLocalServer.PluginMetadata
    ): McpRuntimeDescriptor.Remote = McpRuntimeDescriptor.Remote(
        endpoint = requireNotNull(metadata.endpoint) {
            "Missing endpoint for remote plugin ${metadata.id}"
        },
        connectionType = requireNotNull(metadata.connectionType) {
            "Missing connection type for remote plugin ${metadata.id}"
        },
        bearerToken = metadata.bearerToken,
        headers = metadata.headers.orEmpty()
    )
}

// ==================== 数据类定义 ====================

/** 统一的工具信息数据类 */
private data class UnifiedToolInfo(
    val name: String,
    val description: String,
    val inputSchema: String
)
