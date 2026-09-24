package com.ai.assistance.operit.core.tools.system

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.ai.assistance.operit.util.AppLogger

/**
 * 无障碍服务自启（platform 签名特权档）。
 *
 * priv-app 持 WRITE_SECURE_SETTINGS 后直写 enabled_accessibility_services，
 * 取代手动开启引导（步骤 4 遗留项，步骤 6 落地）。
 *
 * 无障碍服务本体在 ROM 内建的 OperitProvider 模块
 * （com.ai.assistance.operit.provider，platform 签名随 ROM 分发），此处直钉
 * UIAccessibilityService 组件登记（Binder 交付服务 RemoteBinderService 不进
 * enabled_accessibility_services，两者组件名不同，action 解析会误中后者故不采用）。
 */
object AccessibilityAutoEnable {
    private const val TAG = "AccessibilityAutoEnable"
    private const val PROVIDER_PACKAGE_NAME = "com.ai.assistance.operit.provider"
    private const val PROVIDER_ACCESSIBILITY_COMPONENT =
        "$PROVIDER_PACKAGE_NAME/.UIAccessibilityService"

    /**
     * 确保无障碍 provider 服务已登记进系统启用清单。
     * @return true 表示已登记（含此前已登记的情况）
     */
    fun ensureEnabled(context: Context): Boolean {
        val appContext = context.applicationContext
        val component = ComponentName.unflattenFromString(PROVIDER_ACCESSIBILITY_COMPONENT)
            ?: return false
        if (!isProviderComponentAvailable(appContext, component)) {
            AppLogger.w(TAG, "OperitProvider not present on this ROM")
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

    private fun isProviderComponentAvailable(context: Context, component: ComponentName): Boolean {
        return try {
            context.packageManager.getServiceInfo(component, 0) != null
        } catch (e: Exception) {
            AppLogger.e(TAG, "isProviderComponentAvailable failed", e)
            false
        }
    }
}
