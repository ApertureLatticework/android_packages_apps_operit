package com.ai.assistance.operit.provider

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Binder 交付服务：把 [UIAccessibilityService] 持有的 [IAccessibilityProvider] 桩
 * 交给主应用。无障碍服务本体由系统绑定存活，本服务仅作跨进程取桩入口。
 */
class RemoteBinderService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return currentBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 纯绑定型服务，不应被 start；防呆直接停
        stopSelf(startId)
        return START_NOT_STICKY
    }

    companion object {
        @Volatile
        private var currentBinder: IBinder? = null

        fun attach(binder: IBinder) {
            currentBinder = binder
        }

        fun detach() {
            currentBinder = null
        }
    }
}
