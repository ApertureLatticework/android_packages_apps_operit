package com.ai.assistance.operit.core.tools.defaultTool.privileged

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.ai.assistance.operit.core.tools.AITool
import com.ai.assistance.operit.core.tools.defaultTool.accessbility.AccessibilitySystemOperationTools
import com.ai.assistance.operit.core.tools.system.privileged.PrivilegedSystemApi
import com.ai.assistance.operit.data.model.AppOperationData
import com.ai.assistance.operit.data.model.StringResultData
import com.ai.assistance.operit.data.model.ToolResult
import com.ai.assistance.operit.util.AppLogger
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * PRIVILEGED 档系统操作工具：
 * - settings put/get → WRITE_SECURE_SETTINGS 直写（public API，权限由 privapp 白名单授予）
 * - install → PackageInstaller 会话（INSTALL_PACKAGES，静默安装）
 * - stopApp → ActivityManager.forceStopPackage（@hide 反射，FORCE_STOP_PACKAGES）
 * 其余操作继承无障碍/标准实现。
 */
open class PrivilegedSystemOperationTools(context: Context) : AccessibilitySystemOperationTools(context) {

    companion object {
        private const val TAG = "PrivilegedSystemOperationTools"
        private const val INSTALL_TIMEOUT_MS = 120_000L
    }

    /** 安装/卸载状态回调广播 */
    private val installStatusAction = "com.ai.assistance.operit.PRIVILEGED_INSTALL_STATUS"

    override suspend fun modifySystemSetting(tool: AITool): ToolResult {
        val setting = tool.parameters.find { it.name == "setting" }?.value ?: ""
        val value = tool.parameters.find { it.name == "value" }?.value ?: ""
        val namespace = tool.parameters.find { it.name == "namespace" }?.value ?: "system"

        if (setting.isBlank() || value.isBlank()) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Must provide setting and value parameters"
            )
        }

        return try {
            val ok = withContext(Dispatchers.IO) {
                when (namespace) {
                    "global" -> Settings.Global.putString(context.contentResolver, setting, value)
                    "secure" -> Settings.Secure.putString(context.contentResolver, setting, value)
                    "system" -> Settings.System.putString(context.contentResolver, setting, value)
                    else -> return ToolResult(
                        toolName = tool.name,
                        success = false,
                        result = StringResultData(""),
                        error = "Namespace must be one of: system, secure, global"
                    )
                }
            }
            if (ok) {
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = StringResultData(
                        "Successfully set $namespace/$setting to '$value' via privileged settings write"
                    ),
                    error = ""
                )
            } else {
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Settings write rejected: WRITE_SECURE_SETTINGS not granted for namespace '$namespace'."
                )
            }
        } catch (e: SecurityException) {
            AppLogger.e(TAG, "modifySystemSetting security exception", e)
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Failed to set setting: ${e.message}. Requires WRITE_SECURE_SETTINGS."
            )
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error setting system setting", e)
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error setting system setting: ${e.message}"
            )
        }
    }

    override suspend fun installApp(tool: AITool): ToolResult {
        val apkPath = tool.parameters.find { it.name == "path" }?.value ?: ""
        if (apkPath.isBlank()) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Must provide apk path parameter"
            )
        }
        val file = File(apkPath)
        if (!file.exists()) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "APK file does not exist: $apkPath"
            )
        }

        return try {
            val ok = withTimeoutOrNull(INSTALL_TIMEOUT_MS) {
                installViaPackageInstaller(file)
            }
            if (ok == true) {
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = AppOperationData(
                        operationType = "install",
                        packageName = file.name,
                        success = true,
                        details = "Installed $apkPath via PackageInstaller (privileged silent install)"
                    ),
                    error = ""
                )
            } else {
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = when (ok) {
                        null -> "Install timed out after ${INSTALL_TIMEOUT_MS / 1000}s"
                        else -> "PackageInstaller session rejected the install (status=$ok). Requires INSTALL_PACKAGES."
                    }
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error installing app", e)
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error installing app: ${e.message}"
            )
        }
    }

    override suspend fun stopApp(tool: AITool): ToolResult {
        val packageName = tool.parameters.find { it.name == "package_name" }?.value ?: ""
        if (packageName.isBlank()) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Must provide package_name parameter"
            )
        }
        return try {
            val ok = PrivilegedSystemApi.forceStopPackage(context, packageName)
            if (ok) {
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = AppOperationData(
                        operationType = "stop",
                        packageName = packageName,
                        success = true,
                        details = "Force stopped $packageName via ActivityManager (privileged)"
                    ),
                    error = ""
                )
            } else {
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Failed to force stop app. Requires FORCE_STOP_PACKAGES."
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error force stopping app", e)
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error force stopping app: ${e.message}"
            )
        }
    }

    /** PackageInstaller 静默安装，挂起等待状态回调（status==0 成功） */
    private suspend fun installViaPackageInstaller(file: File): Boolean =
        suspendCancellableCoroutine { cont ->
            val installer = context.packageManager.packageInstaller
            val params = android.content.pm.PackageInstaller.SessionParams(
                android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL
            )
            val sessionId = installer.createSession(params)
            val session = installer.openSession(sessionId)

            val statusIntent = Intent(installStatusAction).setPackage(context.packageName)
            val statusSender = PendingIntent.getBroadcast(
                context,
                sessionId,
                statusIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            ).intentSender

            val receiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(ctx: Context, intent: Intent) {
                    val status = intent.getIntExtra(android.content.pm.PackageInstaller.EXTRA_STATUS, Int.MIN_VALUE)
                    try {
                        context.unregisterReceiver(this)
                    } catch (_: Exception) {
                    }
                    if (cont.isActive) cont.resume(status == android.content.pm.PackageInstaller.STATUS_SUCCESS)
                }
            }
            val filter = android.content.IntentFilter(installStatusAction)
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }

            try {
                session.openWrite("operit_apk", 0, file.length()).use { out ->
                    file.inputStream().use { it.copyTo(out) }
                }
                session.commit(statusSender)
            } catch (e: Exception) {
                session.abandon()
                context.unregisterReceiver(receiver)
                if (cont.isActive) cont.resume(false)
                return@suspendCancellableCoroutine
            }

            cont.invokeOnCancellation {
                try {
                    session.abandon()
                } catch (_: Exception) {
                }
                try {
                    context.unregisterReceiver(receiver)
                } catch (_: Exception) {
                }
            }
        }
}
