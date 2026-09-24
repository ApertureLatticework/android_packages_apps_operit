package com.ai.assistance.operit.ui.features.packages.screens.mcp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ai.assistance.operit.data.mcp.MCPRepository
import com.ai.assistance.operit.data.mcp.MCPLocalServer
import kotlinx.coroutines.launch
import android.content.Context

/** ViewModel for MCP 远程服务器管理（remote-only：本地安装线已裁撤） */
class MCPViewModel(
    private val repository: MCPRepository,
    private val context: Context
) : ViewModel() {

    init {
        // 同步已安装状态
        viewModelScope.launch { repository.syncInstalledStatus() }
    }

    /** Adds a remote server to the repository */
    fun addRemoteServer(server: MCPLocalServer.PluginMetadata) {
        viewModelScope.launch {
            repository.addRemoteServer(server)
        }
    }

    /** Updates a remote server's metadata */
    fun updateRemoteServer(server: MCPLocalServer.PluginMetadata) {
        viewModelScope.launch {
            repository.updateRemoteServer(server)
        }
    }

    /** 删除远程服务器 */
    fun removeRemoteServer(server: MCPLocalServer.PluginMetadata) {
        viewModelScope.launch {
            repository.removeRemoteServer(server.id)
        }
    }

    suspend fun generatePluginDescription(
        server: MCPLocalServer.PluginMetadata,
        pluginName: String
    ): Result<String> {
        return repository.generatePluginDescription(
            pluginId = server.id,
            pluginName = pluginName
        )
    }

    /** 刷新插件列表 */
    fun refreshPluginList() {
        viewModelScope.launch {
            repository.syncInstalledStatus()
        }
    }

    /** 同步所有插件的安装状态 */
    fun syncInstalledStatus() {
        viewModelScope.launch { repository.syncInstalledStatus() }
    }

    /** ViewModel Factory */
    class Factory(
        private val repository: MCPRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MCPViewModel::class.java)) {
                return MCPViewModel(repository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
