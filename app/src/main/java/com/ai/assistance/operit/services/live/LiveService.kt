package com.ai.assistance.operit.services.live

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ai.assistance.operit.R
import com.ai.assistance.operit.core.application.ForegroundServiceCompat
import com.ai.assistance.operit.core.tools.system.AccessibilityAutoEnable
import com.ai.assistance.operit.data.preferences.AndroidPermissionLevel
import com.ai.assistance.operit.data.preferences.androidPermissionPreferences
import com.ai.assistance.operit.ui.main.MainActivity
import com.ai.assistance.operit.util.AppLogger

/**
 * Live 常驻前台服务（步骤 6）。
 *
 * 职责：
 * - 常驻保活，为主屏观察通道（LiveScreenMirror）与副屏执行通道（步骤 7）提供宿主
 * - 特权档下开机自启时自动登记无障碍 provider 服务（platform 签名直写 secure settings）
 *
 * 镜像显示器由消费方按引用计数 acquire/release，服务本体不空转帧泵。
 */
class LiveService : Service() {

    companion object {
        private const val TAG = "LiveService"
        private const val CHANNEL_ID = "live_service"
        private const val NOTIFICATION_ID = 4201

        /**
         * 启动 Live 服务：特权档下常驻（开机自启与应用启动都会走到这里）。
         * 非特权档不启动——观察与执行通道在该档均不可用。
         */
        fun ensureStarted(context: Context) {
            val preferredLevel = runCatching {
                context.androidPermissionPreferences.getPreferredPermissionLevel()
            }.getOrNull()
            if (preferredLevel != AndroidPermissionLevel.PRIVILEGED) {
                return
            }
            val intent = Intent(context, LiveService::class.java)
            context.startForegroundService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(), foregroundTypes())

        // platform 签名特权档：开机即自动登记无障碍 provider（步骤 4 遗留项落地）
        val accessibilityReady = AccessibilityAutoEnable.ensureEnabled(this)
        if (!accessibilityReady) {
            AppLogger.w(TAG, "Accessibility auto-enable unavailable on this tier/environment")
        }
        AppLogger.i(TAG, "LiveService started, accessibilityReady=$accessibilityReady")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 常驻语义：被杀后系统重建，观察通道由消费方重新 acquire
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        AppLogger.i(TAG, "LiveService destroyed")
    }

    private fun foregroundTypes(): Int {
        return ForegroundServiceCompat.buildTypes(
            dataSync = false,
            specialUse = true
        )
    }

    private fun buildNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_simple_foreground)
            .setContentTitle(getString(R.string.live_service_notification_title))
            .setContentText(getString(R.string.live_service_notification_text))
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.live_service_notification_title),
            NotificationManager.IMPORTANCE_MIN
        )
        manager.createNotificationChannel(channel)
    }
}
