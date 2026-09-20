package com.ai.assistance.operit.core.tools.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.ai.assistance.operit.util.AppLogger

/**
 * 无障碍服务自启（platform 签名特权档）。
 *
 * priv-app 持 WRITE_SECURE_SETTINGS 后直写 enabled_accessibility_services，
 * 取代手动开启引导（步骤 4 遗留项，步骤 6 落地）。
 *
 * 无障碍服务本体在伴侣应用 com.ai.assistance.operit.provider（assets/accessibility.apk
 * 安装），此处按 action 解析出真实组件名再登记，不硬编码类名。
 */
object AccessibilityAutoEnable {
    private const val TAG = "AccessibilityAutoEnable"
    private const val PROVIDER_PACKAGE_NAME = "com.ai.assistance.operit.provider"
    private const val PROVIDER_ACTION = "com.ai.assistance.operit.provider.IAccessibilityProvider"

    /**
     * 确保无障碍 provider 服务已登记进系统启用清单。
     * @return true 表示已登记（含此前已登记的情况）
     */
    fun ensureEnabled(context: Context): Boolean {
        val appContext = context.applicationContext
        val component = resolveProviderComponent(appContext)
        if (component == null) {
            AppLogger.w(TAG, "Provider service not resolved; is accessibility.apk installed?")
            return false
        }

        return try {
            val componentString = component.flattenToString()
            val current = Settings.Secure.getString(
                appContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""

            if (componentString in current.split(':')) {
                ensureAccessibilityMasterSwitch(appContext)
                return true
            }

            val next = if (current.isBlank()) componentString else "$current:$componentString"
            Settings.Secure.putString(
                appContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                next
            )
            ensureAccessibilityMasterSwitch(appContext)
            AppLogger.i(TAG, "Accessibility provider enabled via secure settings: $componentString")
            true
        } catch (e: SecurityException) {
            // 非 priv 环境（无 WRITE_SECURE_SETTINGS）：显式失败，调用方走手动开启引导
            Log.e(TAG, "ensureEnabled needs WRITE_SECURE_SETTINGS (privileged tier)", e)
            false
        } catch (e: Exception) {
            AppLogger.e(TAG, "ensureEnabled failed", e)
            false
        }
    }

    private fun ensureAccessibilityMasterSwitch(context: Context) {
        val enabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED,
            0
        )
        if (enabled != 1) {
            Settings.Secure.putInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                1
            )
        }
    }

    private fun resolveProviderComponent(context: Context): ComponentName? {
        return try {
            val intent = Intent(PROVIDER_ACTION).setPackage(PROVIDER_PACKAGE_NAME)
            val resolveInfo = context.packageManager.resolveService(intent, 0) ?: return null
            ComponentName(
                resolveInfo.serviceInfo.packageName,
                resolveInfo.serviceInfo.name
            )
        } catch (e: Exception) {
            AppLogger.e(TAG, "resolveProviderComponent failed", e)
            null
        }
    }
}
