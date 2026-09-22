package com.ai.assistance.operit.core.tools.agent

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.app.ActivityOptions
import android.view.Surface
import com.ai.assistance.operit.core.tools.system.privileged.PrivilegedSystemApi
import com.ai.assistance.operit.util.AppLogger
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 原生副屏后端（步骤 7）：priv-app 直调 DisplayManager 建 TRUSTED+OWN_FOCUS 副屏，
 * 取代 Shower（KSU daemon + LSPosed + 桥接 App）整线。
 *
 * 机制映射（LOS 23.2 源码验证依据见 docs/TODO/.../7_VirtualDisplayBackend.md）：
 * - TRUSTED（ADD_TRUSTED_DISPLAY）：副屏被系统视为可信真实屏
 * - OWN_FOCUS：副屏独立焦点域，主屏焦点不受影响，无需 framework 补丁
 * - SHOULD_SHOW_SYSTEM_DECORATIONS：副屏带系统栏，贴近真实屏观感
 * - 输入：INJECT_EVENTS 直注入携带 displayId；启动：ActivityOptions.setLaunchDisplayId
 *
 * 渲染面：虚拟显示器输出到 ImageReader；消费方按需 refreshLatest 后取缓存帧副本，
 * 静止画面不依赖新帧到达。无编码器，无视频流回调（Shower 时代概念，随线删除）。
 */
object NativeVirtualDisplay {
    private const val TAG = "NativeVirtualDisplay"
    private const val DISPLAY_NAME_PREFIX = "OperitVD"
    private const val MAX_IMAGES = 3

    // DisplayManager 隐藏旗标字面量（进树后可改常量直引，值与 LOS 23.2 一致）
    private const val FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS = 1 shl 9   // 512
    private const val FLAG_TRUSTED = 1 shl 10                          // 1024
    private const val FLAG_OWN_FOCUS = 1 shl 11                        // 2048

    private val VD_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
            FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS or
            FLAG_TRUSTED or
            FLAG_OWN_FOCUS

    private val sessions = ConcurrentHashMap<String, Session>()

    fun hasSession(agentId: String): Boolean = sessions.containsKey(agentId)

    fun getSession(agentId: String): Session? = sessions[agentId]

    /**
     * 创建或复用副屏会话。
     * @return null 表示创建失败（ADD_TRUSTED_DISPLAY 缺失等），调用方显式失败
     */
    fun ensureDisplay(context: Context, agentId: String, width: Int, height: Int, dpi: Int): Session? {
        sessions[agentId]?.let { existing ->
            if (existing.width == width && existing.height == height) {
                return existing
            }
            existing.shutdown()
        }

        val appContext = context.applicationContext
        val reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, MAX_IMAGES)
        val displayManager = appContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

        val vd: VirtualDisplay = try {
            displayManager.createVirtualDisplay(
                "$DISPLAY_NAME_PREFIX-$agentId",
                width,
                height,
                dpi,
                reader.surface,
                VD_FLAGS
            ) ?: run {
                AppLogger.e(TAG, "createVirtualDisplay returned null for $agentId")
                reader.close()
                return null
            }
        } catch (e: Exception) {
            // 非 priv 环境（无 ADD_TRUSTED_DISPLAY）在此抛 SecurityException：显式失败，不降级
            AppLogger.e(TAG, "createVirtualDisplay failed for $agentId", e)
            reader.close()
            return null
        }

        val session = Session(
            agentId = agentId,
            virtualDisplay = vd,
            reader = reader,
            width = width,
            height = height
        )
        sessions[agentId] = session
        AppLogger.d(TAG, "Native virtual display created: agent=$agentId displayId=${vd.display.displayId} ${width}x$height")
        return session
    }

    fun getDisplayId(agentId: String): Int? = sessions[agentId]?.displayId

    fun getDisplayId(): Int? = getDisplayId("default")

    fun getVideoSize(agentId: String): Pair<Int, Int>? =
        sessions[agentId]?.let { it.width to it.height }

    fun shutdown(agentId: String) {
        sessions.remove(agentId)?.shutdown()
    }

    fun shutdownAll() {
        val toStop = sessions.keys.toList()
        toStop.forEach { shutdown(it) }
    }

    /**
     * 单个副屏会话。所有输入方法经 PrivilegedSystemApi 按屏注入；
     * 帧缓存由 refreshLatest 消费方驱动刷新，读侧一律拿副本。
     */
    class Session internal constructor(
        val agentId: String,
        private val virtualDisplay: VirtualDisplay,
        private val reader: ImageReader,
        val width: Int,
        val height: Int
    ) {
        val displayId: Int = virtualDisplay.display.displayId

        private val frameLock = Any()
        private var cachedFrame: Bitmap? = null
        @Volatile
        private var shutdown = false

        /** 拉取新帧刷新缓存（无新帧保持旧缓存），随后返回整帧副本；调用方负责 recycle */
        fun refreshAndCaptureBitmap(): Bitmap? {
            refreshLatest()
            synchronized(frameLock) {
                return cachedFrame?.copy(Bitmap.Config.ARGB_8888, false)
            }
        }

        /** 拉取新帧并返回缩放预览副本（悬浮窗小窗渲染用）；调用方负责 recycle */
        fun refreshAndCapturePreview(maxWidthPx: Int): Bitmap? {
            refreshLatest()
            synchronized(frameLock) {
                val frame = cachedFrame ?: return null
                if (frame.width <= maxWidthPx) {
                    return frame.copy(Bitmap.Config.ARGB_8888, false)
                }
                val scale = maxWidthPx.toFloat() / frame.width
                return Bitmap.createScaledBitmap(
                    frame,
                    maxWidthPx,
                    (frame.height * scale).toInt().coerceAtLeast(1),
                    true
                )
            }
        }

        suspend fun requestScreenshotPng(): ByteArray? = withContext(Dispatchers.IO) {
            val bitmap = refreshAndCaptureBitmap() ?: return@withContext null
            try {
                ByteArrayOutputStream().use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        return@withContext null
                    }
                    out.toByteArray()
                }
            } catch (e: Exception) {
                AppLogger.e(TAG, "requestScreenshotPng failed", e)
                null
            } finally {
                bitmap.recycle()
            }
        }

        fun launchApp(context: Context, packageName: String): Boolean {
            if (packageName.isBlank()) return false
            val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: run {
                AppLogger.w(TAG, "[$agentId] no launch intent for $packageName")
                return false
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val options = ActivityOptions.makeBasic().setLaunchDisplayId(displayId)
            return try {
                context.startActivity(intent, options.toBundle())
                true
            } catch (e: Exception) {
                AppLogger.e(TAG, "[$agentId] launchApp($packageName) on display $displayId failed", e)
                false
            }
        }

        fun tap(x: Int, y: Int): Boolean =
            PrivilegedSystemApi.injectTapOnDisplay(x, y, displayId)

        fun swipe(startX: Int, startY: Int, endX: Int, endY: Int, durationMs: Long): Boolean =
            PrivilegedSystemApi.injectSwipeOnDisplay(startX, startY, endX, endY, durationMs, displayId)

        fun touchDown(x: Int, y: Int): Boolean = injectMotion(
            android.view.MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(),
            downTime = android.os.SystemClock.uptimeMillis(),
            eventTime = android.os.SystemClock.uptimeMillis()
        )

        fun touchMove(x: Int, y: Int): Boolean = injectMotion(
            android.view.MotionEvent.ACTION_MOVE, x.toFloat(), y.toFloat(),
            downTime = android.os.SystemClock.uptimeMillis(),
            eventTime = android.os.SystemClock.uptimeMillis()
        )

        fun touchUp(x: Int, y: Int): Boolean = injectMotion(
            android.view.MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(),
            downTime = android.os.SystemClock.uptimeMillis(),
            eventTime = android.os.SystemClock.uptimeMillis()
        )

        /** 整份 MotionEvent 参数转发（悬浮窗触控直通，保留时序与精度语义） */
        fun injectTouchEvent(
            action: Int,
            x: Float,
            y: Float,
            downTime: Long,
            eventTime: Long,
            pressure: Float,
            size: Float,
            metaState: Int,
            xPrecision: Float,
            yPrecision: Float,
            deviceId: Int,
            edgeFlags: Int
        ): Boolean = injectMotion(
            action, x, y, downTime, eventTime, pressure, size,
            metaState, xPrecision, yPrecision, deviceId, edgeFlags
        )

        fun key(keyCode: Int): Boolean = keyWithMeta(keyCode, 0)

        fun keyWithMeta(keyCode: Int, metaState: Int): Boolean =
            PrivilegedSystemApi.injectKeyEventOnDisplay(keyCode, metaState, displayId)

        fun shutdown() {
            if (shutdown) return
            shutdown = true
            sessions.remove(agentId, this)
            try {
                virtualDisplay.release()
            } catch (e: Exception) {
                AppLogger.e(TAG, "[$agentId] release virtual display failed", e)
            }
            synchronized(frameLock) {
                cachedFrame?.recycle()
                cachedFrame = null
            }
            try {
                reader.close()
            } catch (e: Exception) {
                AppLogger.e(TAG, "[$agentId] close ImageReader failed", e)
            }
            AppLogger.d(TAG, "[$agentId] session shut down, displayId=$displayId")
        }

        private fun injectMotion(
            action: Int,
            x: Float,
            y: Float,
            downTime: Long,
            eventTime: Long,
            pressure: Float = 1f,
            size: Float = 1f,
            metaState: Int = 0,
            xPrecision: Float = 1f,
            yPrecision: Float = 1f,
            deviceId: Int = 0,
            edgeFlags: Int = 0
        ): Boolean = PrivilegedSystemApi.injectMotionEventOnDisplay(
            action, x, y, downTime, eventTime, pressure, size,
            metaState, xPrecision, yPrecision, deviceId, edgeFlags, displayId
        )

        private fun refreshLatest() {
            if (shutdown) return
            var image: Image? = null
            try {
                image = reader.acquireLatestImage() ?: return
                val width = image.width
                val height = image.height
                if (width <= 0 || height <= 0) return

                val plane = image.planes[0]
                val pixelStride = plane.pixelStride
                val rowStride = plane.rowStride
                val rowPadding = rowStride - pixelStride * width

                val bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(plane.buffer)
                val cropped = Bitmap.createBitmap(bitmap, 0, 0, width, height)
                bitmap.recycle()

                synchronized(frameLock) {
                    val old = cachedFrame
                    cachedFrame = cropped
                    old?.recycle()
                }
            } catch (e: Exception) {
                AppLogger.e(TAG, "[$agentId] refreshLatest failed", e)
            } finally {
                try {
                    image?.close()
                } catch (_: Exception) {
                }
            }
        }
    }
}
