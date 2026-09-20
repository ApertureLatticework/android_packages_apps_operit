package com.ai.assistance.operit.core.tools.system.privileged

import android.content.Context
import com.ai.assistance.operit.core.tools.system.AndroidPermissionLevel
import com.ai.assistance.operit.core.tools.system.action.AccessibilityActionListener
import com.ai.assistance.operit.core.tools.system.action.ActionListener

/**
 * PRIVILEGED 档 UI 操作监听器：组合无障碍通道（用户操作监听本依赖 AccessibilityService），
 * 档位标识为 PRIVILEGED。
 */
class PrivilegedActionListener(context: Context) : ActionListener {
    private val delegate = AccessibilityActionListener(context)

    override suspend fun startListening(onAction: (ActionListener.ActionEvent) -> Unit): ActionListener.ListeningResult =
        delegate.startListening(onAction)

    override suspend fun stopListening(): Boolean = delegate.stopListening()

    override fun getPermissionLevel(): AndroidPermissionLevel = AndroidPermissionLevel.PRIVILEGED

    override suspend fun isAvailable(): Boolean = delegate.isAvailable()

    override suspend fun requestPermission(onResult: (Boolean) -> Unit) = delegate.requestPermission(onResult)

    override suspend fun hasPermission(): ActionListener.PermissionStatus = delegate.hasPermission()

    override fun initialize() = delegate.initialize()

    override fun isListening(): Boolean = delegate.isListening()
}
