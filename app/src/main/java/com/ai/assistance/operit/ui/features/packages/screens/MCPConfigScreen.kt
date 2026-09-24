package com.ai.assistance.operit.ui.features.packages.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ai.assistance.operit.ui.components.CustomScaffold
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import com.ai.assistance.operit.data.mcp.MCPLocalServer
import com.ai.assistance.operit.data.mcp.MCPRepository
import com.ai.assistance.operit.ui.features.packages.components.dialogs.MCPServerDetailsDialog
import com.ai.assistance.operit.ui.features.packages.dialogs.MCPPackageDetailsDialog
import com.ai.assistance.operit.ui.features.packages.screens.mcp.viewmodel.MCPViewModel
import com.ai.assistance.operit.util.AppLogger
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import com.ai.assistance.operit.R
import org.json.JSONObject

import java.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.ai.assistance.operit.ui.features.startup.screens.LocalPluginLoadingState

/** MCP配置屏幕 - 极简风格界面，专注于插件快速部署 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPConfigScreen(
    onNavigateToMCPMarket: () -> Unit = {},
    searchQuery: String = ""
) {
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity
    val mcpLocalServer = remember { MCPLocalServer.getInstance(context) }
    val mcpRepository = remember { MCPRepository(context) }

    val scope = rememberCoroutineScope()

    val pluginLoadingState = LocalPluginLoadingState.current

    // 实例化ViewModel
    val viewModel = remember {
        MCPViewModel.Factory(mcpRepository, context).create(MCPViewModel::class.java)
    }


    // 状态收集
    val serverStatusMap = mcpLocalServer.serverStatus.collectAsState().value
    val mcpConfigSnapshot = mcpLocalServer.mcpConfig.collectAsState().value
    val discoveredInstalledPluginIds = mcpRepository.installedPluginIds.collectAsState().value
    // remote-only：已配置插件即远程插件
    val remotePluginIds = remember(mcpConfigSnapshot) {
        mcpConfigSnapshot.pluginMetadata.keys.toSet()
    }
    val visiblePluginIds = remember(remotePluginIds, discoveredInstalledPluginIds) {
        remotePluginIds + discoveredInstalledPluginIds
    }

    // 部署状态
    


    // 标记是否已经执行过初始化时的自动启动
    var initialAutoStartPerformed = remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    var isToolsLoading by remember { mutableStateOf(true) }
    var pendingPluginId by remember { mutableStateOf<String?>(null) }
    var toolRefreshTrigger by remember { mutableStateOf(0) }

    // Freeze list order within this screen session (avoid jumping when status changes)
    var lockedPluginOrder by remember { mutableStateOf<List<String>?>(null) }

    suspend fun refreshMcpScreen() {
        if (isRefreshing) return
        isRefreshing = true
        try {
            mcpRepository.refreshPluginList()
            lockedPluginOrder = null
        } finally {
            isRefreshing = false
        }
    }

    fun awaitPluginVisible(pluginId: String, onDone: () -> Unit) {
        pendingPluginId = pluginId
        scope.launch {
            try {
                refreshMcpScreen()
                withTimeoutOrNull(20_000) {
                    mcpLocalServer.mcpConfig.first { config ->
                        config.pluginMetadata.containsKey(pluginId)
                    }
                }
            } finally {
                pendingPluginId = null
                onDone()
            }
        }
    }

    // 在应用启动时检查自动启动设置，而不是等待UI完全加载
    LaunchedEffect(Unit) {
        // 仅在首次加载时执行一次
        if (!initialAutoStartPerformed.value) {
            com.ai.assistance.operit.util.AppLogger.d("MCPConfigScreen", "初始化 - 检查服务器状态")

            refreshMcpScreen()

            // 只记录服务器状态，不再重复启动服务器(已由 Application 中的 initAndAutoStartPlugins 控制)
            val anyServerRunning = visiblePluginIds.any { pluginId ->
                mcpLocalServer.isServerLikelyRunning(pluginId)
            }
            if (anyServerRunning) {
                com.ai.assistance.operit.util.AppLogger.d("MCPConfigScreen", "MCP服务器已在运行")
            } else {
                com.ai.assistance.operit.util.AppLogger.d("MCPConfigScreen", "MCP服务器未运行")
            }

            // 读取并记录已安装的MCP插件列表，但不执行任何操作
            com.ai.assistance.operit.util.AppLogger.d("MCPConfigScreen", "已安装的MCP插件列表:")
            visiblePluginIds.forEach { pluginId ->
                try {
                    val isEnabled = mcpLocalServer.isServerEnabled(pluginId) // 从配置读取
                    com.ai.assistance.operit.util.AppLogger.d("MCPConfigScreen", "插件ID: $pluginId, 已启用: $isEnabled")
                } catch (e: Exception) {
                    com.ai.assistance.operit.util.AppLogger.e("MCPConfigScreen", "无法读取插件 $pluginId 的启用状态: ${e.message}")
                }
            }

            initialAutoStartPerformed.value = true
            toolRefreshTrigger++
        }
    }

    // 界面状态
    var selectedPluginForDetails by remember {
        mutableStateOf<MCPLocalServer.PluginMetadata?>(
                null
        )
    }
    var selectedPluginForToolDetails by remember {
        mutableStateOf<MCPLocalServer.PluginMetadata?>(null)
    }
    // 添加导入对话框状态（remote-only：远程连接与配置导入双 tab）
    var showImportDialog by remember { mutableStateOf(false) }
    var pluginNameInput by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }
    var importTabIndex by remember { mutableStateOf(0) } // 0: 远程连接, 1: 配置导入

    // 新增：远程服务相关状态
    var remoteEndpointInput by remember { mutableStateOf("") }
    var remoteConnectionType by remember { mutableStateOf("httpStream") }
    var remoteConnectionTypeExpanded by remember { mutableStateOf(false) }
    var remoteBearerToken by remember { mutableStateOf("") }
    var remoteHeaders by remember { mutableStateOf<List<EditableHeader>>(emptyList()) }
    
    // 新增：配置导入相关状态
    var configJsonInput by remember { mutableStateOf("") }

    // 新增：远程服务编辑对话框状态
    var showRemoteEditDialog by remember { mutableStateOf(false) }
    var editingRemoteServer by remember { mutableStateOf<MCPLocalServer.PluginMetadata?>(null) }

    val importDialogMaxContentHeight = (LocalConfiguration.current.screenHeightDp * 0.65f).dp



    // Effect to fetch and display tools when MCP servers start
    val isPluginLoading by pluginLoadingState.isVisible.collectAsState()
    val wasPluginLoading = remember { mutableStateOf(isPluginLoading) }

    LaunchedEffect(isPluginLoading) {
        if (wasPluginLoading.value && !isPluginLoading) {
            // Loading has just finished, trigger a refresh.
            isToolsLoading = true
            lockedPluginOrder = null
            toolRefreshTrigger++
        }
        wasPluginLoading.value = isPluginLoading
    }
    
    // 存储每个插件的工具信息
    var pluginToolsMap by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    // 计算插件启动统计 - 只统计已启用的插件
    val totalEnabledPlugins = remember(visiblePluginIds) {
        visiblePluginIds.count { pluginId -> mcpLocalServer.isServerEnabled(pluginId) }
    }
    val successfulToolRequests = remember { mutableStateOf(0) }
    
    // 更新成功请求工具的插件数量
    LaunchedEffect(pluginToolsMap) {
        successfulToolRequests.value = pluginToolsMap.filter { it.value.isNotEmpty() }.size
    }

    val computedSortedPluginIds = remember(
        visiblePluginIds,
        pluginToolsMap,
        serverStatusMap,
        mcpConfigSnapshot
    ) {
        visiblePluginIds
            .toList()
            .sortedWith(
                compareBy<String> { pluginId ->
                    val enabled = mcpLocalServer.isServerEnabled(pluginId)
                    val loaded = pluginToolsMap[pluginId]?.isNotEmpty() == true
                    when {
                        enabled && loaded -> 0
                        enabled -> 1
                        else -> 2
                    }
                }.thenBy { pluginId ->
                    getPluginDisplayName(pluginId, mcpRepository).lowercase(Locale.getDefault())
                }
            )
    }

    // Lock order once tools are loaded (so the initial "good" sort is applied, then frozen)
    LaunchedEffect(isToolsLoading, visiblePluginIds, toolRefreshTrigger) {
        if (lockedPluginOrder == null && visiblePluginIds.isNotEmpty() && !isToolsLoading) {
            lockedPluginOrder = computedSortedPluginIds
        }
    }

    val sortedPluginIds = remember(lockedPluginOrder, visiblePluginIds, computedSortedPluginIds) {
        val visibleSet = visiblePluginIds.toSet()
        val base = (lockedPluginOrder ?: computedSortedPluginIds)
        val kept = base.filter { visibleSet.contains(it) }
        val missing = visibleSet - kept.toSet()
        if (missing.isEmpty()) {
            kept
        } else {
            kept + missing.sortedBy { pluginId ->
                getPluginDisplayName(pluginId, mcpRepository).lowercase(Locale.getDefault())
            }
        }
    }

    val displayedPluginIds = remember(
        sortedPluginIds,
        searchQuery,
        mcpConfigSnapshot,
        pluginToolsMap
    ) {
        val searchText = searchQuery.trim()
        if (searchText.isEmpty()) {
            sortedPluginIds
        } else {
            sortedPluginIds.filter { pluginId ->
                mcpPluginMatchesSearch(
                    pluginId = pluginId,
                    displayName = getPluginDisplayName(pluginId, mcpRepository),
                    metadata = mcpConfigSnapshot.pluginMetadata[pluginId],
                    toolNames = pluginToolsMap[pluginId],
                    searchText = searchText
                )
            }
        }
    }

    LaunchedEffect(toolRefreshTrigger, mcpConfigSnapshot) {
        if (toolRefreshTrigger == 0) {
            return@LaunchedEffect
        }

        isToolsLoading = true
        if (visiblePluginIds.isEmpty()) {
            AppLogger.d("MCPConfigScreen", "No configured plugins, clearing tool list.")
            pluginToolsMap = emptyMap()
            isToolsLoading = false
            return@LaunchedEffect
        }

        AppLogger.d("MCPConfigScreen", "Fetching tools for configured services...")

        val toolsMap = mutableMapOf<String, List<String>>()
        // Keep discovering tools after the page becomes interactive; only the full-screen mask is bounded.
        val fullscreenLoadingTimeout = launch {
            delay(2_000)
            isToolsLoading = false
        }

        try {
            for (pluginId in visiblePluginIds) {
                try {
                    val toolNames = mcpRepository.getRemoteToolNames(pluginId)

                    if (toolNames.isNotEmpty()) {
                        toolsMap[pluginId] = toolNames
                        AppLogger.d("MCPConfigScreen", "Plugin $pluginId has ${toolNames.size} tools: ${toolNames.joinToString(", ")}")
                    } else {
                        AppLogger.d("MCPConfigScreen", "Plugin $pluginId: no tools found.")
                    }
                } catch (e: Exception) {
                    AppLogger.e("MCPConfigScreen", "Error getting tools for plugin $pluginId: ${e.message}")
                }
            }

            // 更新工具映射
            pluginToolsMap = toolsMap

            if (toolsMap.isNotEmpty()) {
                val totalTools = toolsMap.values.sumOf { it.size }
                AppLogger.i("MCPConfigScreen", "Loaded $totalTools tools from ${toolsMap.size} plugins")
            } else {
                AppLogger.i("MCPConfigScreen", "No tools found for any installed plugins.")
            }
        } catch (e: Exception) {
            AppLogger.e("MCPConfigScreen", "Error fetching tools", e)
            Toast.makeText(context, context.getString(R.string.tools_load_error, e.message), Toast.LENGTH_SHORT).show()
        } finally {
            fullscreenLoadingTimeout.cancel()
            isToolsLoading = false
        }
    }


    // 插件详情对话框（remote-only：无本地安装路径与 stdio 配置编辑面）
    if (selectedPluginForDetails != null) {
        MCPServerDetailsDialog(
                server = selectedPluginForDetails!!,
                onDismiss = { selectedPluginForDetails = null },
                onUninstall = { server ->
                    viewModel.removeRemoteServer(server)
                    selectedPluginForDetails = null
                }
        )
    }

    if (selectedPluginForToolDetails != null) {
        MCPPackageDetailsDialog(
                server = selectedPluginForToolDetails!!,
                onDismiss = { selectedPluginForToolDetails = null }
        )
    }





    // 新增：远程服务编辑对话框
    if (showRemoteEditDialog && editingRemoteServer != null) {
        RemoteServerEditDialog(
            server = editingRemoteServer!!,
            onDismiss = {
                showRemoteEditDialog = false
                editingRemoteServer = null
            },
            onSave = { updatedServer ->
                viewModel.updateRemoteServer(updatedServer)
                showRemoteEditDialog = false
                editingRemoteServer = null
                Toast.makeText(context, context.getString(R.string.remote_service_updated, updatedServer.name), Toast.LENGTH_SHORT).show()
            },
            onRegenerateDescription = { server, pluginName ->
                viewModel.generatePluginDescription(
                    server = server,
                    pluginName = pluginName
                )
            }
        )
    }




    // 导入插件对话框
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(stringResource(R.string.import_or_connect_mcp_service)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = importDialogMaxContentHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 添加顶部导入方式选择（remote-only：远程连接与配置导入）
                    Column {
                        ScrollableTabRow(
                            selectedTabIndex = importTabIndex,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            edgePadding = 8.dp,
                            divider = {},
                            indicator = { tabPositions ->
                                if (importTabIndex < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[importTabIndex])
                                    )
                                }
                            }
                        ) {
                        Tab(
                            selected = importTabIndex == 0,
                            onClick = { importTabIndex = 0 },
                            text = { 
                                Text(
                                    stringResource(R.string.connect_remote_service),
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                ) 
                            }
                        )
                        Tab(
                            selected = importTabIndex == 1,
                            onClick = { importTabIndex = 1 },
                            text = {
                                Text(
                                    stringResource(R.string.mcp_config_import),
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    when (importTabIndex) {
                        0 -> {
                            // 连接远程服务
                            Text(stringResource(R.string.enter_remote_service_info), style = MaterialTheme.typography.bodyMedium)

                            OutlinedTextField(
                                value = remoteEndpointInput,
                                onValueChange = { remoteEndpointInput = it },
                                label = { Text(stringResource(R.string.host_address)) },
                                placeholder = { Text("http://127.0.0.1:8752") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val connectionTypes = listOf("httpStream", "sse")
                            ExposedDropdownMenuBox(
                                expanded = remoteConnectionTypeExpanded,
                                onExpandedChange = { remoteConnectionTypeExpanded = !remoteConnectionTypeExpanded },
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    value = remoteConnectionType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.connection_type)) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = remoteConnectionTypeExpanded) },
                                )
                                ExposedDropdownMenu(
                                    expanded = remoteConnectionTypeExpanded,
                                    onDismissRequest = { remoteConnectionTypeExpanded = false },
                                ) {
                                    connectionTypes.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                remoteConnectionType = selectionOption
                                                remoteConnectionTypeExpanded = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = remoteBearerToken,
                                onValueChange = { remoteBearerToken = it },
                                label = { Text(stringResource(R.string.mcp_remote_bearer_token)) },
                                placeholder = { Text(stringResource(R.string.mcp_remote_bearer_token_hint)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            RemoteHeadersEditor(
                                headers = remoteHeaders,
                                onHeadersChange = { remoteHeaders = it }
                            )
                        }
                        1 -> {
                            // 配置导入
                            Text(stringResource(R.string.mcp_paste_config_json), style = MaterialTheme.typography.bodyMedium)

                            OutlinedTextField(
                                value = configJsonInput,
                                onValueChange = { configJsonInput = it },
                                label = { Text(stringResource(R.string.mcp_config_content)) },
                                placeholder = { Text("{\n  \"mcpServers\": {\n    \"playwright\": {\n      \"command\": \"npx\",\n      \"args\": [\"@playwright/mcp@latest\"]\n    }\n  }\n}") },
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                maxLines = 8
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            TextButton(
                                onClick = {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                                    intent.setDataAndType(android.net.Uri.parse(mcpLocalServer.getConfigFilePath()), "application/json")
                                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val fileIntent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                                        fileIntent.setDataAndType(android.net.Uri.parse("file://${mcpLocalServer.getConfigFilePath()}"), "*/*")
                                        fileIntent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                        try {
                                            context.startActivity(android.content.Intent.createChooser(fileIntent, context.getString(R.string.mcp_open_config_file)))
                                        } catch (e2: Exception) {
                                            Toast.makeText(context, context.getString(R.string.mcp_config_file_location, mcpLocalServer.getConfigFilePath()), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.mcp_open_config_file), fontSize = 12.sp)
                            }
                        }
                    }
                    
                    if (importTabIndex != 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(stringResource(R.string.service_metadata), style = MaterialTheme.typography.titleSmall)
                        
                        OutlinedTextField(
                            value = pluginNameInput,
                            onValueChange = { newValue ->
                                // 只允许英文字母、数字和下划线
                                val filtered = newValue.filter { it.isLetterOrDigit() || it == '_' }
                                pluginNameInput = filtered
                            },
                            label = { Text(stringResource(R.string.plugin_name)) },
                            placeholder = { Text(stringResource(R.string.my_mcp_plugin)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            supportingText = { 
                                Text(
                                    stringResource(R.string.plugin_name_description),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val isConfigImport = importTabIndex == 1
                        val isRemoteConnect = !isConfigImport && remoteEndpointInput.isNotBlank() && pluginNameInput.isNotBlank()

                        if (isConfigImport) {
                            if (configJsonInput.isNotBlank()) {
                                isImporting = true
                                scope.launch {
                                    AppLogger.d("MCPConfigScreen", "开始导入配置，内容长度: ${configJsonInput.length}")
                                    try {
                                        val result = mcpLocalServer.mergeConfigFromJson(configJsonInput)
                                        result.onSuccess { count ->
                                            AppLogger.i("MCPConfigScreen", "配置导入成功，合并了 $count 个服务器")
                                            Toast.makeText(context, context.getString(R.string.mcp_merged_servers, count), Toast.LENGTH_SHORT).show()
                                            refreshMcpScreen()
                                            configJsonInput = ""
                                            showImportDialog = false
                                        }.onFailure { error ->
                                            AppLogger.e("MCPConfigScreen", "配置导入失败: ${error.message}", error)
                                            Toast.makeText(context, context.getString(R.string.mcp_merge_failed, error.message ?: "Unknown error"), Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        AppLogger.e("MCPConfigScreen", "配置导入异常", e)
                                        Toast.makeText(context, context.getString(R.string.mcp_import_exception, e.message ?: "Unknown error"), Toast.LENGTH_LONG).show()
                                    } finally {
                                        isImporting = false
                                    }
                                }
                            } else {
                                Toast.makeText(context, context.getString(R.string.mcp_please_enter_config), Toast.LENGTH_SHORT).show()
                            }
                        } else if (isRemoteConnect) {
                            // 检查插件ID是否冲突
                            val proposedId = pluginNameInput.replace(" ", "_").lowercase()
                            if (visiblePluginIds.contains(proposedId)) {
                                Toast.makeText(context, context.getString(R.string.plugin_already_exists, pluginNameInput), Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isImporting = true
                            val importId = proposedId

                            // 创建服务器对象（描述将由自动生成功能填充）
                            val server = MCPLocalServer.PluginMetadata(
                                id = importId,
                                name = pluginNameInput,
                                description = "", // 将由自动生成功能填充
                                logoUrl = "",
                                author = "",
                                isInstalled = true,
                                version = "1.0.0",
                                updatedAt = "",
                                longDescription = "", // 将由自动生成功能填充
                                endpoint = remoteEndpointInput,
                                connectionType = remoteConnectionType,
                                bearerToken = if (remoteBearerToken.isNotBlank()) remoteBearerToken else null,
                                headers = remoteHeaders.toHeaderMap()
                            )

                            // 远程服务直接保存到仓库
                            viewModel.addRemoteServer(server)
                            Toast.makeText(context, context.getString(R.string.remote_service_added, server.name), Toast.LENGTH_SHORT).show()

                            // 清空输入并关闭对话框
                            pluginNameInput = ""
                            remoteEndpointInput = ""
                            remoteConnectionType = "httpStream"
                            remoteConnectionTypeExpanded = false
                            remoteBearerToken = ""
                            remoteHeaders = emptyList()
                            showImportDialog = false

                            awaitPluginVisible(importId) {
                                isImporting = false
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.enter_complete_remote_info), Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isImporting &&
                             ((importTabIndex == 0 && remoteEndpointInput.isNotBlank() && pluginNameInput.isNotBlank()) ||
                              (importTabIndex == 1 && configJsonInput.isNotBlank()))
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(when(importTabIndex) {
                        1 -> stringResource(R.string.mcp_merge_config)
                        else -> stringResource(R.string.connect)
                    })
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pluginNameInput = ""
                    remoteEndpointInput = ""
                    remoteConnectionType = "httpStream"
                    remoteConnectionTypeExpanded = false
                    remoteBearerToken = ""
                    remoteHeaders = emptyList()
                    configJsonInput = ""
                    showImportDialog = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val isAnyLoading =
        isRefreshing || isImporting || isPluginLoading || pendingPluginId != null

    val isFullscreenLoading =
        isToolsLoading || (visiblePluginIds.isEmpty() && (isAnyLoading || !initialAutoStartPerformed.value))

    if (isFullscreenLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    CustomScaffold(
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 启动插件按钮
                    FloatingActionButton(
                        onClick = {
                            if (!isAnyLoading) {
                                val lifecycleScope = activity?.lifecycleScope
                                if (lifecycleScope != null) {
                                    pluginLoadingState.reset() // 确保每次都重置状态
                                    pluginLoadingState.show()
                                    pluginLoadingState.initializeMCPServer(context, lifecycleScope)
                                } else {
                                    Toast.makeText(context, "Failed to start plugin loading", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        if (isAnyLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.start_plugin))
                        }
                    }

                    // 市场按钮
                    FloatingActionButton(
                        onClick = onNavigateToMCPMarket,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Store, contentDescription = stringResource(R.string.mcp_market))
                    }

                    // 导入按钮
                    FloatingActionButton(
                        onClick = {
                            showImportDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.import_action))
                    }
                }
            }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
                // 主界面内容
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        top = 8.dp,
                        end = 8.dp,
                        bottom = 200.dp // 为悬浮按钮留出空间
                    )
                ) {
                    // 状态指示器
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.mcp_management),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = when {
                                                    totalEnabledPlugins == 0 -> Color.Gray
                                                    successfulToolRequests.value == totalEnabledPlugins -> Color.Green
                                                    successfulToolRequests.value > 0 -> Color(0xFFFFA500) // Orange
                                                    else -> Color.Red
                                                },
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    )
                                    Text(
                                        text = "${successfulToolRequests.value}/$totalEnabledPlugins",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    
                    // 插件列表标题
                    if (displayedPluginIds.isNotEmpty()) {
                        
                        // 插件列表
                        items(items = displayedPluginIds, key = { it }) { pluginId ->
                            // 获取插件启用状态 - 从配置读取
                            val pluginEnabledState = remember(pluginId) {
                                mutableStateOf(mcpLocalServer.isServerEnabled(pluginId))
                            }

                            // 获取插件运行状态
                            val pluginRunningState = remember(pluginId) {
                                mutableStateOf(mcpLocalServer.isServerLikelyRunning(pluginId))
                            }

                            // 监听服务器状态变化
                            LaunchedEffect(pluginId) {
                                mcpLocalServer.serverStatus.collect { _ ->
                                    pluginRunningState.value = mcpLocalServer.isServerLikelyRunning(pluginId)
                                }
                            }
                            
                            // 监听配置变化（isEnabled状态）
                            LaunchedEffect(pluginId) {
                                mcpLocalServer.mcpConfig.collect { _ ->
                                    pluginEnabledState.value = mcpLocalServer.isServerEnabled(pluginId)
                                }
                            }

                            PluginListItem(
                                    pluginId = pluginId,
                                    displayName = getPluginDisplayName(pluginId, mcpRepository),
                                    isOfficial = pluginId.startsWith("official_"),
                                    toolNames = pluginToolsMap[pluginId] ?: emptyList(), // 传递工具信息
                                    onClick = {
                                        selectedPluginForDetails = getPluginAsServer(
                                            pluginId,
                                            mcpRepository,
                                            mcpConfigSnapshot,
                                            discoveredInstalledPluginIds,
                                            context
                                        )
                                    },
                                    onToolsClick = {
                                        selectedPluginForToolDetails = getPluginAsServer(
                                            pluginId,
                                            mcpRepository,
                                            mcpConfigSnapshot,
                                            discoveredInstalledPluginIds,
                                            context
                                        )
                                    },
                                    onEdit = {
                                        // 设置要编辑的服务器并显示对话框
                                        val serverToEdit = getPluginAsServer(
                                            pluginId,
                                            mcpRepository,
                                            mcpConfigSnapshot,
                                            discoveredInstalledPluginIds,
                                            context
                                        )
                                        if(serverToEdit != null){
                                            editingRemoteServer = serverToEdit
                                            showRemoteEditDialog = true
                                        }
                                    },
                                    isEnabled = pluginEnabledState.value,
                                    onEnabledChange = { isChecked ->
                                        scope.launch {
                                            mcpLocalServer.setServerEnabled(pluginId, isChecked)
                                        }
                                    },
                                    isRunning = pluginRunningState.value,
                                    isDeployed = true,
                                    isConfigValid = true,
                                    invalidConfigReason = null
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    } else {
                        // 无插件提示
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Extension,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            stringResource(
                                                if (searchQuery.isBlank()) {
                                                    R.string.no_plugins
                                                } else {
                                                    R.string.no_matching_plugins_found
                                                }
                                            ),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            stringResource(R.string.use_import_function_to_add),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
    }
}

// 从插件ID中提取显示名称
private fun getPluginDisplayName(pluginId: String, mcpRepository: MCPRepository): String {
    val pluginInfo = mcpRepository.getInstalledPluginInfo(pluginId)
    val originalName = pluginInfo?.name

    if (originalName != null && originalName.isNotBlank()) {
        return originalName
    }

    return when {
        pluginId.contains("/") -> pluginId.split("/").last().replace("-", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        pluginId.startsWith("official_") ->
            pluginId.removePrefix("official_").replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        else -> pluginId
    }
}

private fun mcpPluginMatchesSearch(
    pluginId: String,
    displayName: String,
    metadata: MCPLocalServer.PluginMetadata?,
    toolNames: List<String>?,
    searchText: String
): Boolean {
    val searchableText =
        buildList {
            add(pluginId)
            add(displayName)
            metadata?.let { pluginMetadata ->
                add(pluginMetadata.id)
                add(pluginMetadata.name)
                add(pluginMetadata.description)
                add(pluginMetadata.author)
                add(pluginMetadata.version)
                add(pluginMetadata.longDescription)
                pluginMetadata.endpoint?.let { add(it) }
            }
            toolNames?.forEach { toolName -> add(toolName) }
        }

    return searchableText.any { text -> text.contains(searchText, ignoreCase = true) }
}

// 获取插件元数据
private fun getPluginAsServer(
    pluginId: String,
    mcpRepository: MCPRepository,
    mcpConfigSnapshot: MCPLocalServer.MCPConfig,
    discoveredInstalledPluginIds: Set<String>,
    context: Context
): MCPLocalServer.PluginMetadata? {
    val metadataFromConfig = mcpConfigSnapshot.pluginMetadata[pluginId]
    val pluginInfo = metadataFromConfig ?: mcpRepository.getInstalledPluginInfo(pluginId)

    if (metadataFromConfig != null) {
        return metadataFromConfig.copy(isInstalled = true)
    }

    // 尝试从内存中的服务器列表查找
    val existingServer = mcpRepository.mcpServers.value.find { it.id == pluginId }

    // 如果在列表中找到，直接使用
    if (existingServer != null) {
        return existingServer.copy(isInstalled = true)
    }

    val displayName = getPluginDisplayName(pluginId, mcpRepository)

    return MCPLocalServer.PluginMetadata(
        id = pluginId,
        name = displayName,
        description = pluginInfo?.description ?: context.getString(R.string.local_installed_plugin),
        logoUrl = "",
        author = pluginInfo?.author ?: context.getString(R.string.local_installation),
        isInstalled = true,
        version = pluginInfo?.version ?: context.getString(R.string.local_version),
        updatedAt = "",
        longDescription = pluginInfo?.longDescription
            ?: (pluginInfo?.description ?: context.getString(R.string.local_installed_plugin)),
        endpoint = pluginInfo?.endpoint,
        connectionType = pluginInfo?.connectionType,
        bearerToken = pluginInfo?.bearerToken,
        headers = pluginInfo?.headers
    )
}

@Composable
private fun PluginListItem(
    pluginId: String,
    displayName: String,
    isOfficial: Boolean,
    toolNames: List<String>,
    onClick: () -> Unit,
    onToolsClick: () -> Unit,
    onEdit: () -> Unit,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    isRunning: Boolean = false,
    isDeployed: Boolean = false,
    isConfigValid: Boolean = true,
    invalidConfigReason: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 主要信息行：图标 + 名称 + 开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 紧凑的插件图标
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )

                    // 运行状态指示点
                    if (isRunning) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .background(
                                    color = Color(0xFF4CAF50),
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // 插件名称和状态
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        // 状态标签
                        if (isOfficial) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.official),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp
                                )
                            }
                        }
                        
                        if (!invalidConfigReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = stringResource(R.string.mcp_config_invalid_tag),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    if (!invalidConfigReason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = invalidConfigReason,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 紧凑的开关
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onEnabledChange,
                    enabled = isConfigValid,
                    modifier = Modifier.scale(0.8f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            // 工具标签区域（如果有）
            if (toolNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f))
                        .clickable(onClick = onToolsClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(toolNames.take(5)) { toolName ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = toolName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (toolNames.size > 5) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.more) + "${toolNames.size - 5}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // 操作按钮区域
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 编辑按钮
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.edit),
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteServerEditDialog(
    server: MCPLocalServer.PluginMetadata,
    onDismiss: () -> Unit,
    onSave: (MCPLocalServer.PluginMetadata) -> Unit,
    onRegenerateDescription: suspend (MCPLocalServer.PluginMetadata, String) -> Result<String>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember(server.id) { mutableStateOf(server.name) }
    var description by remember(server.id) { mutableStateOf(server.description) }
    var endpoint by remember(server.id) { mutableStateOf(server.endpoint ?: "") }
    var connectionType by remember(server.id) { mutableStateOf(server.connectionType ?: "httpStream") }
    var bearerToken by remember(server.id) { mutableStateOf(server.bearerToken ?: "") }
    var headers by remember(server.id) { mutableStateOf(server.headers.toEditableHeaders()) }
    val connectionTypes = listOf("httpStream", "sse")
    var expanded by remember(server.id) { mutableStateOf(false) }
    var isRegeneratingDescription by remember(server.id) { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_remote_service)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                isRegeneratingDescription = true
                                try {
                                    onRegenerateDescription(server, name)
                                        .onSuccess { generatedDescription ->
                                            description = generatedDescription
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.mcp_regenerate_description_success),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .onFailure { error ->
                                            Toast.makeText(
                                                context,
                                                context.getString(
                                                    R.string.mcp_regenerate_description_failed,
                                                    error.message ?: context.getString(R.string.unknown_error)
                                                ),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                } finally {
                                    isRegeneratingDescription = false
                                }
                            }
                        },
                        enabled = !isRegeneratingDescription && name.isNotBlank()
                    ) {
                        if (isRegeneratingDescription) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                if (isRegeneratingDescription) {
                                    R.string.mcp_regenerating_description
                                } else {
                                    R.string.mcp_regenerate_description
                                }
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text(stringResource(R.string.host_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            value = connectionType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.connection_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            connectionTypes.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        connectionType = selectionOption
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = bearerToken,
                        onValueChange = { bearerToken = it },
                        label = { Text(stringResource(R.string.mcp_remote_bearer_token)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.mcp_remote_bearer_token_hint)) }
                    )

                    RemoteHeadersEditor(
                        headers = headers,
                        onHeadersChange = { headers = it }
                    )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val normalizedName = name.trim()
                    val normalizedDescription = description.trim()
                    val updatedServer = server.copy(
                        name = normalizedName,
                        description = normalizedDescription,
                        longDescription = normalizedDescription,
                        endpoint = endpoint,
                        connectionType = connectionType,
                        bearerToken = if (bearerToken.isNotBlank()) bearerToken else null,
                        headers = headers.toHeaderMap()
                    )
                    onSave(updatedServer)
                },
                enabled = !isRegeneratingDescription && name.isNotBlank() && endpoint.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private data class EditableHeader(
    val id: String = UUID.randomUUID().toString(),
    val key: String = "",
    val value: String = ""
)

private fun Map<String, String>?.toEditableHeaders(): List<EditableHeader> {
    return this
        ?.map { (key, value) -> EditableHeader(key = key, value = value) }
        .orEmpty()
}

private fun List<EditableHeader>.toHeaderMap(): Map<String, String>? {
    val headerEntries = LinkedHashMap<String, String>()

    for (header in this) {
        val key = header.key.trim()
        if (key.isBlank()) {
            continue
        }
        headerEntries[key] = header.value
    }

    return headerEntries.ifEmpty { null }
}

@Composable
private fun RemoteHeadersEditor(
    headers: List<EditableHeader>,
    onHeadersChange: (List<EditableHeader>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.mcp_remote_custom_headers),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = stringResource(R.string.mcp_remote_custom_headers_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        headers.forEachIndexed { index, header ->
            key(header.id) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = header.key,
                        onValueChange = { newKey ->
                            onHeadersChange(
                                headers.toMutableList().apply {
                                    this[index] = this[index].copy(key = newKey)
                                }
                            )
                        },
                        label = { Text(stringResource(R.string.mcp_remote_header_name)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = header.value,
                        onValueChange = { newValue ->
                            onHeadersChange(
                                headers.toMutableList().apply {
                                    this[index] = this[index].copy(value = newValue)
                                }
                            )
                        },
                        label = { Text(stringResource(R.string.mcp_remote_header_value)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            onHeadersChange(headers.toMutableList().apply { removeAt(index) })
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.mcp_remote_remove_header)
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                onHeadersChange(headers + EditableHeader())
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(stringResource(R.string.mcp_remote_add_header))
        }
    }
}
