package com.ai.assistance.operit.data.mcp

import android.content.Context
import android.content.SharedPreferences
import com.ai.assistance.operit.util.AppLogger
import com.ai.assistance.operit.core.tools.AIToolHandler
import com.ai.assistance.operit.core.tools.mcp.MCPManager
import com.ai.assistance.operit.util.OperitPaths
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

/**
 * 统一的MCP配置管理中心（remote-only）
 *
 * 负责管理远程 MCP 服务的配置、元数据与运行状态：
 * - 官方MCP配置格式的读写（远程条目）
 * - 插件元数据管理
 * - 服务器状态管理
 * - 统一存储在下载/Operit/mcp_plugins目录
 *
 * 本地 stdio 插件线已随 terminal 线裁撤：mcpServers 命令型条目与 local 型元数据
 * 在加载迁移时即被丢弃，配置结构仅承载远程插件。
 */
class MCPLocalServer private constructor(private val context: Context) {
    companion object {
        private const val TAG = "MCPLocalServer"
        private const val PREFS_NAME = "mcp_local_server_prefs"

        // 配置文件名称
        private const val MCP_CONFIG_FILE = "mcp_config.json"
        private const val SERVER_STATUS_FILE = "server_status.json"

        @Volatile private var INSTANCE: MCPLocalServer? = null

        fun getInstance(context: Context): MCPLocalServer {
            return INSTANCE
                    ?: synchronized(this) {
                        INSTANCE
                                ?: MCPLocalServer(context.applicationContext).also { INSTANCE = it }
                    }
        }
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 持久化配置
    private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // 配置目录路径
    private val configBaseDir by lazy {
        OperitPaths.mcpPluginsDir()
    }

    // 配置文件路径
    private val mcpConfigFile get() = File(configBaseDir, MCP_CONFIG_FILE)
    private val serverStatusFile get() = File(configBaseDir, SERVER_STATUS_FILE)

    // 配置状态
    private val _mcpConfig = MutableStateFlow(MCPConfig())
    val mcpConfig: StateFlow<MCPConfig> = _mcpConfig.asStateFlow()

    // 插件元数据
    val pluginMetadata: StateFlow<Map<String, PluginMetadata>> = _mcpConfig
        .map { it.pluginMetadata.toMap() }
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyMap())

    // 服务器状态
    private val _serverStatus = MutableStateFlow<Map<String, ServerStatus>>(emptyMap())
    val serverStatus: StateFlow<Map<String, ServerStatus>> = _serverStatus.asStateFlow()

    // Gson实例 - 使用格式化输出
    private val gson = com.google.gson.GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        // 初始化时加载所有配置
        loadAllConfigurations()
    }

    // ==================== 官方MCP配置格式支持 ====================

    /**
     * 官方MCP配置格式数据结构（remote-only）
     */
    @Serializable
    data class MCPConfig(
        @SerializedName("pluginMetadata")
        val pluginMetadata: MutableMap<String, PluginMetadata> = mutableMapOf()
    )

    /**
     * 插件元数据（远程插件）
     */
    @Serializable
    data class PluginMetadata(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("logoUrl")
        val logoUrl: String? = null,
        @SerializedName("author")
        val author: String = "Unknown",
        @SerializedName("isInstalled")
        val isInstalled: Boolean = true,
        @SerializedName("version")
        val version: String = "",
        @SerializedName("updatedAt")
        val updatedAt: String = "",
        @SerializedName("longDescription")
        val longDescription: String = "",
        @SerializedName("endpoint")
        val endpoint: String? = null,
        @SerializedName("connectionType")
        val connectionType: String? = "httpStream",
        @SerializedName("disabled")
        val disabled: Boolean = false,
        // 认证相关字段
        @SerializedName("bearerToken")
        val bearerToken: String? = null,
        @SerializedName("headers")
        val headers: Map<String, String>? = null,
        @SerializedName("installedTime")
        val installedTime: Long = System.currentTimeMillis(),
        // 市场配置（来自 GitHub Issue）
        @SerializedName("marketConfig")
        val marketConfig: String? = null
    )

    /**
     * 服务器运行状态
     * 注意：启用/禁用状态在PluginMetadata.disabled字段
     */
    @Serializable
    data class ServerStatus(
        @SerializedName("serverId")
        val serverId: String,
        @SerializedName("lastStartTime")
        val lastStartTime: Long = 0L,
        @SerializedName("lastStopTime")
        val lastStopTime: Long = 0L,
        @SerializedName("errorMessage")
        val errorMessage: String? = null,
        @SerializedName("cachedTools")
        val cachedTools: List<CachedToolInfo>? = null,
        @SerializedName("toolsCachedTime")
        val toolsCachedTime: Long = 0L
    )

    /**
     * 缓存的工具信息
     */
    @Serializable
    data class CachedToolInfo(
        @SerializedName("name")
        val name: String,
        @SerializedName("description")
        val description: String = "",
        @SerializedName("inputSchema")
        val inputSchema: String = "{}", // JSON字符串形式的schema
        @SerializedName("cachedAt")
        val cachedAt: Long = System.currentTimeMillis()
    )

    // ==================== 配置文件操作 ====================

    /**
     * 重新加载配置文件（用于用户手动编辑配置后刷新）
     */
    suspend fun reloadConfigurations() {
        withContext(Dispatchers.IO) {
            loadAllConfigurations()
            AppLogger.d(TAG, "配置已重新加载")
        }
    }

    /**
     * 加载所有配置文件
     *
     * 迁移语义：旧配置里的 mcpServers（stdio 命令型）与 local 型插件元数据随
     * 本地运行时裁撤已不可运行，加载时直接丢弃；pluginMetadata 中 type 缺失或
     * 非 "remote" 的条目一并清除，随后将清理结果重写落盘。
     */
    private fun loadAllConfigurations() {
        try {
            var migrated = false
            // 加载MCP配置
            if (mcpConfigFile.exists()) {
                val configJson = mcpConfigFile.readText()
                val sanitizedJson = sanitizeRemoteOnlyConfig(configJson)
                if (sanitizedJson == null) {
                    // 配置已是 remote-only 形态，直接反序列化
                    _mcpConfig.value = gson.fromJson(configJson, MCPConfig::class.java)
                        ?: MCPConfig()
                } else {
                    // 迁移清理发生：以清理后的 JSON 反序列化并异步重写落盘
                    _mcpConfig.value = gson.fromJson(sanitizedJson, MCPConfig::class.java)
                        ?: MCPConfig()
                    migrated = true
                }
                if (migrated) {
                    coroutineScope.launch {
                        saveMCPConfig()
                        AppLogger.d(TAG, "已迁移 mcp_config.json：丢弃本地 stdio 条目与 local 型元数据")
                    }
                }
            }

            // 加载服务器状态
            if (serverStatusFile.exists()) {
                val statusJson = serverStatusFile.readText()
                val hasLegacyActiveField = statusJson.contains("\"active\"")
                val typeToken = object : TypeToken<Map<String, ServerStatus>>() {}.type
                val status = gson.fromJson<Map<String, ServerStatus>>(statusJson, typeToken) ?: emptyMap()
                _serverStatus.value = status
                if (hasLegacyActiveField) {
                    coroutineScope.launch {
                        saveServerStatus()
                        AppLogger.d(TAG, "已迁移 server_status.json：移除 legacy active 字段")
                    }
                }
            }

            // 为新配置的服务器初始化状态
            initializeMissingServerStatus()

            AppLogger.d(TAG, "配置加载完成 - 插件元数据: ${_mcpConfig.value.pluginMetadata.size}")
        } catch (e: Exception) {
            AppLogger.e(TAG, "加载配置时出错", e)
        }
    }

    /**
     * 检查并清理旧配置里的本地痕迹。
     *
     * @return null 表示配置已是 remote-only 形态无需清理；否则返回清理后的 JSON 字符串
     */
    private fun sanitizeRemoteOnlyConfig(configJson: String): String? {
        return try {
            val root = JsonParser.parseString(configJson)
            if (!root.isJsonObject) return null
            val rootObj = root.asJsonObject

            var changed = false
            if (rootObj.has("mcpServers")) {
                rootObj.remove("mcpServers")
                changed = true
            }

            val metadataElement = rootObj.get("pluginMetadata")
            if (metadataElement != null && metadataElement.isJsonObject) {
                val metadataObj = metadataElement.asJsonObject
                val iterator = metadataObj.entrySet().iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    val type = entry.value.takeIf { it.isJsonObject }
                        ?.asJsonObject
                        ?.get("type")
                        ?.takeIf { it.isJsonPrimitive }
                        ?.asJsonPrimitive
                        ?.asString
                    if (type != "remote") {
                        iterator.remove()
                        changed = true
                    }
                }
            }

            if (changed) gson.toJson(rootObj) else null
        } catch (e: Exception) {
            AppLogger.e(TAG, "清理本地配置痕迹时出错", e)
            null
        }
    }

    private suspend fun resetRuntimeState(serverId: String) = withContext(Dispatchers.IO) {
        val toolHandler = AIToolHandler.getInstance(context)
        toolHandler.getAllToolNames()
            .filter { it.startsWith("$serverId:") }
            .forEach(toolHandler::unregisterTool)
        MCPManager.getInstance(context).unregisterServer(serverId)
        removeServerStatus(serverId)
    }

    private fun hasRemoteRuntimeConfigurationChanged(
        existing: PluginMetadata,
        replacement: PluginMetadata
    ): Boolean {
        return existing.endpoint != replacement.endpoint ||
            existing.connectionType != replacement.connectionType ||
            existing.bearerToken != replacement.bearerToken ||
            existing.headers.orEmpty() != replacement.headers.orEmpty()
    }

    /**
     * 为新配置的服务器初始化状态
     */
    private fun initializeMissingServerStatus() {
        val currentStatus = _serverStatus.value.toMutableMap()
        var hasNewStatus = false

        val configuredServerIds = _mcpConfig.value.pluginMetadata.keys
        configuredServerIds.forEach { serverId ->
            if (!currentStatus.containsKey(serverId)) {
                currentStatus[serverId] = ServerStatus(
                    serverId = serverId,
                    lastStartTime = 0L,
                    lastStopTime = 0L,
                    errorMessage = null
                )
                hasNewStatus = true
                AppLogger.d(TAG, "初始化服务器状态: $serverId")
            }
        }

        if (hasNewStatus) {
            _serverStatus.value = currentStatus
            coroutineScope.launch {
                saveServerStatus()
            }
        }
    }

    /**
     * 保存MCP配置
     */
    suspend fun saveMCPConfig() {
        try {
            val configJson = gson.toJson(_mcpConfig.value)
            mcpConfigFile.writeText(configJson)
            AppLogger.d(TAG, "MCP配置已保存")
        } catch (e: Exception) {
            AppLogger.e(TAG, "保存MCP配置时出错", e)
        }
    }

    /**
     * 保存服务器状态
     */
    suspend fun saveServerStatus() {
        try {
            val statusJson = gson.toJson(_serverStatus.value)
            serverStatusFile.writeText(statusJson)
            AppLogger.d(TAG, "服务器状态已保存")
        } catch (e: Exception) {
            AppLogger.e(TAG, "保存服务器状态时出错", e)
        }
    }

    // ==================== MCP服务器管理 ====================

    /**
     * 删除插件：移除元数据与运行状态
     */
    suspend fun removeMCPServer(serverId: String) {
        _mcpConfig.update { currentConfig ->
            val newMetadata = currentConfig.pluginMetadata.toMutableMap()
            newMetadata.remove(serverId)
            currentConfig.copy(pluginMetadata = newMetadata)
        }
        saveMCPConfig()
        removeServerStatus(serverId)

        AppLogger.d(TAG, "MCP服务器配置已删除: $serverId")
    }

    /**
     * 合并JSON配置到现有配置（remote-only：stdio 条目由解析器显式拒绝）
     */
    suspend fun mergeConfigFromJson(jsonConfig: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                AppLogger.d(TAG, "开始合并配置，输入长度: ${jsonConfig.length}")
                val parsedConfig = try {
                    McpConfigImportParser.parse(jsonConfig)
                } catch (e: Exception) {
                    AppLogger.e(TAG, "标准 MCP 配置解析失败", e)
                    return@withContext Result.failure(
                        Exception(
                            context.getString(
                                com.ai.assistance.operit.R.string.mcp_local_json_format_error,
                                e.message ?: "配置字段无效"
                            )
                        )
                    )
                }

                val runtimeResetIds = mutableSetOf<String>()
                _mcpConfig.update { currentConfig ->
                    val newMetadata = currentConfig.pluginMetadata.toMutableMap()

                    parsedConfig.servers.forEach { importedServer ->
                        val replacement = createRemoteMetadata(
                            importedServer,
                            newMetadata[importedServer.id]
                        )
                        val existingMetadata = newMetadata[importedServer.id]
                        if (existingMetadata != null &&
                            hasRemoteRuntimeConfigurationChanged(existingMetadata, replacement)) {
                            runtimeResetIds.add(importedServer.id)
                        }
                        newMetadata[importedServer.id] = replacement
                    }

                    currentConfig.copy(pluginMetadata = newMetadata)
                }

                runtimeResetIds.forEach { serverId -> resetRuntimeState(serverId) }
                saveMCPConfig()

                AppLogger.i(
                    TAG,
                    "成功合并 ${parsedConfig.servers.size} 个远程服务器配置"
                )
                Result.success(parsedConfig.servers.size)
            } catch (e: Exception) {
                AppLogger.e(TAG, "合并配置失败: ${e.message}", e)
                Result.failure(Exception(context.getString(com.ai.assistance.operit.R.string.mcp_local_merge_config_failed, e.message)))
            }
        }
    }

    /**
     * 获取配置文件路径
     */
    fun getConfigFilePath(): String = mcpConfigFile.absolutePath

    // ==================== 插件元数据管理 ====================

    /**
     * 添加或更新插件元数据
     */
    suspend fun addOrUpdatePluginMetadata(metadata: PluginMetadata) {
        var shouldResetRuntime = false
        _mcpConfig.update { currentConfig ->
            val newMetadata = currentConfig.pluginMetadata.toMutableMap()
            val existingMetadata = newMetadata[metadata.id]
            shouldResetRuntime =
                existingMetadata != null &&
                    hasRemoteRuntimeConfigurationChanged(existingMetadata, metadata)
            newMetadata[metadata.id] = metadata
            currentConfig.copy(pluginMetadata = newMetadata)
        }
        saveMCPConfig()
        if (shouldResetRuntime) {
            resetRuntimeState(metadata.id)
        }
        AppLogger.d(TAG, "插件元数据已更新: ${metadata.id} - ${metadata.name}")
    }

    /**
     * 删除插件元数据
     */
    suspend fun removePluginMetadata(pluginId: String) {
        _mcpConfig.update { currentConfig ->
            val newMetadata = currentConfig.pluginMetadata.toMutableMap()
            newMetadata.remove(pluginId)
            currentConfig.copy(pluginMetadata = newMetadata)
        }
        saveMCPConfig()
        AppLogger.d(TAG, "插件元数据已删除: $pluginId")
    }

    /**
     * 获取插件元数据
     */
    fun getPluginMetadata(pluginId: String): PluginMetadata? {
        return _mcpConfig.value.pluginMetadata[pluginId]
    }

    /**
     * 获取所有插件元数据
     */
    fun getAllPluginMetadata(): Map<String, PluginMetadata> {
        return _mcpConfig.value.pluginMetadata.toMap()
    }

    // ==================== 服务器状态管理 ====================

    /**
     * 更新服务器状态
     * 注意：启用/禁用状态请使用 setServerEnabled() 方法
     */
    suspend fun updateServerStatus(
        serverId: String,
        errorMessage: String? = null,
        cachedTools: List<CachedToolInfo>? = null,
        lastStartTime: Long? = null,
        lastStopTime: Long? = null
    ) {
        val currentStatus = _serverStatus.value.toMutableMap()
        val existingStatus = currentStatus[serverId] ?: ServerStatus(serverId)

        val updatedStatus = existingStatus.copy(
            errorMessage = errorMessage ?: existingStatus.errorMessage,
            cachedTools = cachedTools ?: existingStatus.cachedTools,
            toolsCachedTime = if (cachedTools != null) System.currentTimeMillis() else existingStatus.toolsCachedTime,
            lastStartTime = lastStartTime ?: existingStatus.lastStartTime,
            lastStopTime = lastStopTime ?: existingStatus.lastStopTime
        )

        currentStatus[serverId] = updatedStatus
        _serverStatus.value = currentStatus
        saveServerStatus()
        AppLogger.d(TAG, "服务器状态已更新: $serverId")
    }

    /**
     * 缓存服务器的工具列表
     */
    suspend fun cacheServerTools(serverId: String, tools: List<CachedToolInfo>) {
        updateServerStatus(serverId = serverId, cachedTools = tools)
        AppLogger.d(TAG, "已缓存服务器 $serverId 的 ${tools.size} 个工具")
    }

    /**
     * 获取缓存的工具列表
     */
    fun getCachedTools(serverId: String): List<CachedToolInfo>? {
        return _serverStatus.value[serverId]?.cachedTools
    }

    /**
     * 检查工具缓存是否有效 (有效期1天)
     */
    fun hasValidToolCache(serverId: String): Boolean {
        val status = _serverStatus.value[serverId] ?: return false

        val cachedTools = status.cachedTools
        val cacheTime = status.toolsCachedTime

        if (cachedTools.isNullOrEmpty() || cacheTime <= 0) {
            return false
        }

        // 缓存有效期为1天
        val oneDayInMillis = 24 * 60 * 60 * 1000L
        return (System.currentTimeMillis() - cacheTime) < oneDayInMillis
    }

    /**
     * 删除服务器状态
     */
    suspend fun removeServerStatus(serverId: String) {
        val currentStatus = _serverStatus.value.toMutableMap()
        currentStatus.remove(serverId)
        _serverStatus.value = currentStatus
        saveServerStatus()
        AppLogger.d(TAG, "服务器状态已删除: $serverId")
    }

    /**
     * 获取服务器状态
     */
    fun getServerStatus(serverId: String): ServerStatus? {
        return _serverStatus.value[serverId]
    }

    /**
     * 获取所有服务器状态
     */
    fun getAllServerStatus(): Map<String, ServerStatus> {
        return _serverStatus.value.toMap()
    }

    /**
     * 基于时间戳推断服务是否处于运行态（近似状态，不是实时状态）
     */
    fun isServerLikelyRunning(serverId: String): Boolean {
        val status = _serverStatus.value[serverId] ?: return false
        return status.lastStartTime > 0L && status.lastStartTime >= status.lastStopTime
    }

    /**
     * 检查服务器是否启用（远程插件从 pluginMetadata.disabled 读取）
     */
    fun isServerEnabled(serverId: String): Boolean {
        val metadata = getPluginMetadata(serverId) ?: return true
        return metadata.disabled != true
    }

    /**
     * 设置服务器启用状态（远程插件写入 pluginMetadata.disabled）
     */
    suspend fun setServerEnabled(serverId: String, enabled: Boolean) {
        val metadata = getPluginMetadata(serverId)
        if (metadata == null) {
            AppLogger.w(TAG, "设置启用状态失败，未找到插件元数据: $serverId")
            return
        }
        addOrUpdatePluginMetadata(metadata.copy(disabled = !enabled))
        AppLogger.d(TAG, "服务器启用状态已更新: $serverId, enabled=$enabled")
    }

    private fun createRemoteMetadata(
        server: RemoteMcpImportedServer,
        existingMetadata: PluginMetadata?
    ): PluginMetadata {
        val baseMetadata = existingMetadata
            ?: PluginMetadata(
                id = server.id,
                name = displayNameForServerId(server.id),
                description = "",
                isInstalled = true,
                version = "1.0.0"
            )

        return baseMetadata.copy(
            isInstalled = true,
            endpoint = server.endpoint,
            connectionType = server.connectionType,
            disabled = server.disabled,
            bearerToken = null,
            headers = server.headers,
            installedTime = System.currentTimeMillis()
        )
    }

    private fun displayNameForServerId(serverId: String): String {
        return serverId
            .replace("_", " ")
            .replace("-", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { character -> character.uppercase() } }
    }

    // ==================== 工具方法 ====================

    /**
     * 导出配置为JSON字符串
     */
    fun exportConfigAsJson(): String {
        val exportData = mapOf(
            "mcpConfig" to _mcpConfig.value,
            "serverStatus" to _serverStatus.value,
            "exportTime" to System.currentTimeMillis(),
            "version" to "1.0"
        )
        return gson.toJson(exportData)
    }

    /**
     * 从JSON字符串导入配置
     */
    suspend fun importConfigFromJson(json: String): Boolean {
        return try {
            val typeToken = object : TypeToken<Map<String, Any>>() {}.type
            val importData = gson.fromJson<Map<String, Any>>(json, typeToken)

            importData["mcpConfig"]?.let { config ->
                val configJson = gson.toJson(config)
                // 导入面同样收敛 remote-only：本地痕迹在落盘前清除
                val sanitizedJson = sanitizeRemoteOnlyConfig(configJson)
                if (sanitizedJson != null) {
                    AppLogger.w(TAG, "导入的配置包含本地 stdio 条目或 local 型元数据，已清除")
                }
                _mcpConfig.value = gson.fromJson(sanitizedJson ?: configJson, MCPConfig::class.java)
                    ?: MCPConfig()
                saveMCPConfig()
            }

            importData["serverStatus"]?.let { status ->
                val statusJson = gson.toJson(status)
                val typeToken3 = object : TypeToken<Map<String, ServerStatus>>() {}.type
                val serverStatus = gson.fromJson<Map<String, ServerStatus>>(statusJson, typeToken3)
                _serverStatus.value = serverStatus
                saveServerStatus()
            }

            AppLogger.d(TAG, "配置导入成功")
            true
        } catch (e: Exception) {
            AppLogger.e(TAG, "导入配置失败", e)
            false
        }
    }

    /**
     * 获取配置目录路径
     */
    fun getConfigDirectory(): String = configBaseDir.absolutePath

    /**
     * 清理无效配置
     */
    suspend fun cleanupInvalidConfigurations() {
        try {
            // 清理无效的服务器状态
            val validPluginIds = _mcpConfig.value.pluginMetadata.keys
            val statusToRemove = _serverStatus.value.keys.filter { it !in validPluginIds }
            if (statusToRemove.isNotEmpty()) {
                val currentStatus = _serverStatus.value.toMutableMap()
                statusToRemove.forEach { serverId ->
                    currentStatus.remove(serverId)
                }
                _serverStatus.value = currentStatus
                saveServerStatus()
                AppLogger.d(TAG, "清理了 ${statusToRemove.size} 个无效的服务器状态")
            }

        } catch (e: Exception) {
            AppLogger.e(TAG, "清理配置时出错", e)
        }
    }
}
