package com.ai.assistance.operit.core.tools.system.live

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ai.assistance.operit.data.preferences.DisplayPreferencesManager
import com.ai.assistance.operit.util.AppLogger
import kotlinx.coroutines.delay

/**
 * Live 主屏观察通道：AUTO_MIRROR 虚拟显示器 + ImageReader 定频取帧。
 *
 * priv-app 持 CAPTURE_VIDEO_OUTPUT 后经 DisplayManager 直建镜像显示器，
 * 无 MediaProjection 授权弹窗；安全页面采集叠加 VIRTUAL_DISPLAY_FLAG_SECURE，
 * 由显示设置 live_secure_capture 开关控制，默认关闭。
 *
 * 生命周期为引用计数：LiveService 与各自动化会话 acquire/release，
 * 计数归零即释放显示器与缓冲队列。
 */
object LiveScreenMirror {
    private const val TAG = "LiveScreenMirror"
    private const val DISPLAY_NAME = "OperitLiveMirror"
    private const val MAX_IMAGES = 3
    private const val FRAME_POLL_INTERVAL_MS = 100L

    private val lock = Any()

    private var referenceCount = 0
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    /** 就绪态：镜像显示器存在。false 时调用方应视为观察通道不可用并显式失败。 */
    val isReady: Boolean
        get() = synchronized(lock) { virtualDisplay != null }

    /**
     * 引用计数 +1 并确保镜像显示器存在。
     * @return false 表示创建失败（权限缺失/显示器服务异常），调用方不得静默降级
     */
    fun acquire(context: Context): Boolean {
        synchronized(lock) {
            if (virtualDisplay != null) {
                referenceCount++
                return true
            }
            val appContext = context.applicationContext
            if (!createMirror(appContext)) {
                return false
            }
            referenceCount = 1
            return true
        }
    }

    /** 引用计数 -1，归零释放全部资源。 */
    fun release() {
        synchronized(lock) {
            referenceCount--
            if (referenceCount > 0) {
                return
            }
            referenceCount = 0
            teardown()
        }
    }

    /**
     * 取最新一帧（RGBA）。无新帧返回 null；返回的 Bitmap 由调用方负责 recycle。
     */
    fun captureLatestBitmap(): Bitmap? {
        val reader = synchronized(lock) { imageReader } ?: return null
        var image: Image? = null
        return try {
            image = reader.acquireLatestImage() ?: return null

            val width = image.width
            val height = image.height
            if (width <= 0 || height <= 0) {
                return null
            }

            val plane = image.planes[0]
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val rowPadding = rowStride - pixelStride * width
            val buffer = plane.buffer

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            val cropped = Bitmap.createBitmap(bitmap, 0, 0, width, height)
            bitmap.recycle()
            cropped
        } catch (e: Exception) {
            AppLogger.e(TAG, "captureLatestBitmap failed", e)
            null
        } finally {
            try {
                image?.close()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * 等待并取首帧/最新帧：镜像刚建立时首个合成周期尚未完成，短轮询等待。
     * 超时返回 null（观察通道不可用），调用方显式处理失败。
     */
    suspend fun captureFrame(maxWaitMs: Long = 600L): Bitmap? {
        val deadline = android.os.SystemClock.uptimeMillis() + maxWaitMs
        while (true) {
            val bitmap = captureLatestBitmap()
            if (bitmap != null) {
                return bitmap
            }
            if (android.os.SystemClock.uptimeMillis() >= deadline) {
                return null
            }
            delay(FRAME_POLL_INTERVAL_MS)
        }
    }

    private fun createMirror(context: Context): Boolean {
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayManager =
                context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)

            val reader = ImageReader.newInstance(
                metrics.widthPixels,
                metrics.heightPixels,
                PixelFormat.RGBA_8888,
                MAX_IMAGES
            )
            imageReader = reader

            var flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
            if (DisplayPreferencesManager.getInstance(context).isLiveSecureCaptureEnabled()) {
                flags = flags or DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE
            }

            virtualDisplay = displayManager.createVirtualDisplay(
                DISPLAY_NAME,
                metrics.widthPixels,
                metrics.heightPixels,
                metrics.densityDpi,
                reader.surface,
                flags
            )
            if (virtualDisplay == null) {
                AppLogger.e(TAG, "createVirtualDisplay returned null (CAPTURE_VIDEO_OUTPUT granted?)")
                imageReader?.close()
                imageReader = null
                return false
            }
            AppLogger.d(
                TAG,
                "Mirror display created: ${metrics.widthPixels}x${metrics.heightPixels}, secure=" +
                    "${DisplayPreferencesManager.getInstance(context).isLiveSecureCaptureEnabled()}"
            )
            true
        } catch (e: Exception) {
            // 非 priv 环境（无 CAPTURE_VIDEO_OUTPUT）在此抛 SecurityException：显式失败，不做降级
            AppLogger.e(TAG, "createMirror failed", e)
            imageReader?.close()
            imageReader = null
            virtualDisplay = null
            false
        }
    }

    private fun teardown() {
        try {
            virtualDisplay?.release()
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error releasing mirror display", e)
        }
        virtualDisplay = null

        try {
            imageReader?.close()
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error closing ImageReader", e)
        }
        imageReader = null
    }
}
