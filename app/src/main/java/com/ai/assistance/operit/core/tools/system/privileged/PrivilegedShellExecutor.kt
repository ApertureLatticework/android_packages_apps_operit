package com.ai.assistance.operit.core.tools.system.privileged

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.InputEvent
import android.view.KeyEvent
import com.ai.assistance.operit.core.tools.system.AndroidPermissionLevel
import com.ai.assistance.operit.core.tools.system.shell.ShellExecutor
import com.ai.assistance.operit.util.AppLogger

/**
 * PRIVILEGED 档 Shell 执行器：受限命令族解析 → SystemApi 直调。
 *
 * 特权应用无 shell uid，不存在通用命令通道；本执行器只翻译既有工具链发出的
 * 已知命令族（input/settings/am force-stop），未识别命令明确失败——
 * 新命令面必须先在 [PrivilegedSystemApi] 落 SystemApi 通路再接入。
 */
class PrivilegedShellExecutor(private val context: Context) : ShellExecutor {
    companion object {
        private const val TAG = "PrivilegedShellExecutor"
    }

    override fun getPermissionLevel(): AndroidPermissionLevel = AndroidPermissionLevel.PRIVILEGED

    /** platform 特权权限在 privapp 白名单授予后即常可用 */
    override fun isAvailable(): Boolean = hasPermission().granted

    override fun hasPermission(): ShellExecutor.PermissionStatus {
        val granted = context.checkSelfPermission(Manifest.permission.INJECT_EVENTS) ==
            PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED
        return if (granted) {
            ShellExecutor.PermissionStatus.granted()
        } else {
            ShellExecutor.PermissionStatus.denied(
                "INJECT_EVENTS/WRITE_SECURE_SETTINGS 未授予：应用需以 platform 签名装入 priv-app 并登记白名单"
            )
        }
    }

    override fun initialize() {}

    /** 特权权限由 ROM 构建期白名单授予，无运行时申请通道，直接回调当前状态 */
    override fun requestPermission(onResult: (Boolean) -> Unit) {
        onResult(hasPermission().granted)
    }

    override suspend fun executeCommand(
        command: String,
        identity: com.ai.assistance.operit.core.tools.system.ShellIdentity
    ): ShellExecutor.CommandResult {
        val trimmed = command.trim()
        return try {
            when {
                trimmed.matches(Regex("input (tap|keyevent|swipe|text).*")) ->
                    executeInputCommand(trimmed)

                trimmed.startsWith("am force-stop ") -> {
                    val pkg = trimmed.removePrefix("am force-stop ").trim()
                    val ok = PrivilegedSystemApi.forceStopPackage(context, pkg)
                    result(ok, if (ok) "force-stopped $pkg" else "forceStopPackage rejected (FORCE_STOP_PACKAGES 未授予)")
                }

                trimmed.startsWith("settings put ") -> {
                    val parts = trimmed.split(Regex("\\s+"))
                    if (parts.size < 4) return result(false, "usage: settings put <namespace> <key> <value>")
                    val namespace = parts[2]
                    val key = parts[3]
                    val value = parts.drop(4).joinToString(" ")
                    val ok = writeSetting(namespace, key, value)
                    result(ok, if (ok) "set $namespace/$key=$value" else "settings write rejected")
                }

                else -> result(
                    false,
                    "PRIVILEGED 档不支持该命令（特权档无 shell uid 通用通道）：$trimmed"
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "executeCommand failed: $command", e)
            ShellExecutor.CommandResult(false, "", e.message ?: "exception", -1)
        }
    }

    override suspend fun startProcess(command: String): com.ai.assistance.operit.core.tools.system.shell.ShellProcess {
        throw UnsupportedOperationException(
            "PRIVILEGED 档不支持持久进程（无 shell uid 通道）：$command"
        )
    }

    private fun executeInputCommand(command: String): ShellExecutor.CommandResult {
        val parts = command.split(Regex("\\s+"))
        return when (parts.getOrNull(1)) {
            "tap" -> {
                val x = parts.getOrNull(2)?.toIntOrNull()
                val y = parts.getOrNull(3)?.toIntOrNull()
                if (x == null || y == null) return result(false, "invalid tap args")
                result(PrivilegedSystemApi.injectTap(x, y), "tap ($x, $y)")
            }
            "swipe" -> {
                val sx = parts.getOrNull(2)?.toIntOrNull()
                val sy = parts.getOrNull(3)?.toIntOrNull()
                val ex = parts.getOrNull(4)?.toIntOrNull()
                val ey = parts.getOrNull(5)?.toIntOrNull()
                val dur = parts.getOrNull(6)?.toLongOrNull() ?: 300L
                if (sx == null || sy == null || ex == null || ey == null) {
                    return result(false, "invalid swipe args")
                }
                result(PrivilegedSystemApi.injectSwipe(sx, sy, ex, ey, dur), "swipe")
            }
            "keyevent" -> {
                val code = parts.getOrNull(2) ?: return result(false, "missing keyevent code")
                val parsed = code.toIntOrNull() ?: runCatching {
                    KeyEvent::class.java.getField(code).getInt(null)
                }.getOrNull() ?: return result(false, "unknown keyevent $code")
                result(PrivilegedSystemApi.injectKey(parsed), "keyevent $code")
            }
            else -> result(false, "unsupported input subcommand: ${parts.getOrNull(1)}")
        }
    }

    private fun writeSetting(namespace: String, key: String, value: String): Boolean {
        val resolver = context.contentResolver
        return when (namespace) {
            "global" -> android.provider.Settings.Global.putString(resolver, key, value)
            "secure" -> android.provider.Settings.Secure.putString(resolver, key, value)
            "system" -> android.provider.Settings.System.putString(resolver, key, value)
            else -> false
        }
    }

    private fun result(ok: Boolean, message: String) = ShellExecutor.CommandResult(
        success = ok,
        stdout = if (ok) message else "",
        stderr = if (ok) "" else message,
        exitCode = if (ok) 0 else 1
    )
}
