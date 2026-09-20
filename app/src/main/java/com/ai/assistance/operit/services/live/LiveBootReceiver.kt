package com.ai.assistance.operit.services.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ai.assistance.operit.util.AppLogger

/**
 * Live 服务开机自启（步骤 6）。
 *
 * 特权档判定收敛在 LiveService.ensureStarted 内；BOOT_COMPLETED 属于
 * 前台服务启动豁免场景，直接转发启动请求。
 */
class LiveBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        LiveService.ensureStarted(context.applicationContext)
        AppLogger.d("LiveBootReceiver", "BOOT_COMPLETED handled")
    }
}
