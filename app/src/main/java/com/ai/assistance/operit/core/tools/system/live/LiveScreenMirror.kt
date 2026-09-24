package com.ai.assistance.operit.core.tools.system.live

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ai.assistance.operit.data.preferences.DisplayPreferencesManager
import com.ai.assistance.operit.util.AppLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Live 主屏观察通道：AUTO_MIRROR 虚拟显示器 + ImageReader 定频取帧 + 连续帧泵。
 *
 * priv-app 持 CAPTURE_VIDEO_OUTPUT 后经 DisplayManager 直建镜像显示器，
 * 无 MediaProjection 授权弹窗；安全页面采集叠加 VIRTUAL_DISPLAY_FLAG_SECURE，
 * 由显示设置 live_secure_capture 开关控制，默认关闭。
 *
 * 帧泵（2026-09-23，live_pipeline_completion 步骤 1）：
 * - OnImageAvailableListener 事件驱动（泵专用单线程 Handler），画面变化即到
 * - 节流窗口 liveFramePumpIntervalMs（显示设置四档 0/250/500/1000，默认 250ms）
 * - conflate 语义（replay=0 + DROP_OLDEST）：慢订阅者永远拿最新帧，旧帧即弃
 * - 无订阅者时事件即弃不复制 Bitmap（保持事件源常开，画面新鲜度语义不依赖轮询）
 *
 * 生命周期为引用计数：LiveService 与各自动化会话 acquire/release，
 * 计数归零即释放显示器与缓冲队列。
 */
object LiveScreenMirror {
    private const val TAG = "LiveScreenMirror"
    private const val DISPLAY_NAME = "OperitLiveMirror"
    private const val MAX_IMAGES = 3
    private const val FRAME_POLL_INTERVAL_MS = 100L
    private const val AWAIT_FRESH_FRAME_TIMEOUT_MS = 2000L

    private val lock = Any()

    private var referenceCount = 0
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    // ==================== 帧泵 ====================

    /** 泵产出的一帧：全分辨率 ARGB_8888，消费方负责 recycle。 */
    data class Frame(
        val bitmap: Bitmap,
        val sequence: Long,
        val timestampMs: Long
    )

    private val _frames = MutableSharedFlow<Frame>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )

    /** 帧流：conflate 语义，订阅者收取泵内最新帧。 */
    val frames: SharedFlow<Frame> = _frames

    @Volatile
    private var frameSequence: Long = 0L

    @Volatile
    private var lastPumpAtMs: Long = 0L

    @Volatile
    private var pumpIntervalMs: Int = 250

    private var pumpThread: HandlerThread? = null
    private var pumpHandler: Handler? = null

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
     * 等待比自己手中序号新的帧。静态画面下 AUTO_MIRROR 不发帧，超时返回 null
     * （画面未变化的显式信号），调用方不得用旧帧冒充新帧。
     */
    suspend fun awaitFreshFrame(
        afterSequence: Long,
        timeoutMs: Long = AWAIT_FRESH_FRAME_TIMEOUT_MS
    ): Frame? {
        return withTimeoutOrNull(timeoutMs) {
            _frames.first { it.sequence > afterSequence }
        }
    }

    /** 泵内最新已产出的序号（不含未到节流窗口的事件）。 */
    fun latestSequence(): Long = frameSequence

    /** 设置面即时应用节流间隔（镜像生命周期内生效，无需重建显示器）。 */
    fun applyPumpIntervalMs(intervalMs: Int) {
        pumpIntervalMs = intervalMs.coerceAtLeast(0)
    }

    /**
     * 取最新一帧（RGBA）。无新帧返回 null；返回的 Bitmap 由调用方负责 recycle。
     */
    fun captureLatestBitmap(): Bitmap? {
        val reader = synchronized(lock) { imageReader } ?: return null
        var image: Image? = null
        return try {
            image = reader.acquireLatestImage() ?: return null
            imageToBitmap(image)
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
        val deadline = SystemClock.uptimeMillis() + maxWaitMs
        while (true) {
            val bitmap = captureLatestBitmap()
            if (bitmap != null) {
                return bitmap
            }
            if (SystemClock.uptimeMillis() >= deadline) {
                return null
            }
            delay(FRAME_POLL_INTERVAL_MS)
        }
    }

    // ==================== 内部 ====================

    private fun imageToBitmap(image: Image): Bitmap? {
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
        return cropped
    }

    private fun onImageAvailable(reader: ImageReader) {
        val image = try {
            reader.acquireLatestImage() ?: return
        } catch (e: Exception) {
            AppLogger.e(TAG, "acquireLatestImage failed in pump", e)
            return
        }
        try {
            // 节流窗口内的变化事件直接丢弃：帧泵语义是"最新帧"，非"全帧"
            val now = SystemClock.uptimeMillis()
            val interval = pumpIntervalMs
            if (interval > 0 && now - lastPumpAtMs < interval) {
                return
            }
            // 无订阅者不复制 Bitmap：事件常开保画面新鲜度语义，分配按需发生
            if (_frames.subscriptionCount.value <= 0) {
                return
            }
            val bitmap = imageToBitmap(image) ?: return
            lastPumpAtMs = now
            frameSequence += 1
            val frame = Frame(
                bitmap = bitmap,
                sequence = frameSequence,
                timestampMs = SystemClock.elapsedRealtime()
            )
            if (!_frames.tryEmit(frame)) {
                // DROP_OLDEST 下不应失败；防御路径显式回收防泄漏
                frame.bitmap.recycle()
            }
        } finally {
            try {
                image.close()
            } catch (_: Exception) {
            }
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

            // 泵线程随显示器生命周期建撤
            val thread = HandlerThread("operit-live-pump").apply { start() }
            val handler = Handler(thread.looper)
            pumpThread = thread
            pumpHandler = handler
            pumpIntervalMs = DisplayPreferencesManager.getInstance(context).getLiveFramePumpIntervalMs()
            lastPumpAtMs = 0L

            val reader = ImageReader.newInstance(
                metrics.widthPixels,
                metrics.heightPixels,
                PixelFormat.RGBA_8888,
                MAX_IMAGES
            )
            reader.setOnImageAvailableListener({ r -> onImageAvailable(r) }, handler)
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
                teardown()
                return false
            }
            AppLogger.d(
                TAG,
                "Mirror display created: ${metrics.widthPixels}x${metrics.heightPixels}, secure=" +
                    "${DisplayPreferencesManager.getInstance(context).isLiveSecureCaptureEnabled()}, pumpInterval=${pumpIntervalMs}ms"
            )
            true
        } catch (e: Exception) {
            // 非 priv 环境（无 CAPTURE_VIDEO_OUTPUT）在此抛 SecurityException：显式失败，不做降级
            AppLogger.e(TAG, "createMirror failed", e)
            teardown()
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

        pumpHandler?.looper?.quitSafely()
        pumpHandler = null
        pumpThread = null
    }
}
