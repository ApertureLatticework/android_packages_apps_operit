package com.ai.assistance.operit.core.tools.system.privileged

import android.content.Context
import android.util.Log
import android.view.Display
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent

/**
 * PRIVILEGED 档 SystemApi 反射通路。
 *
 * Gradle 过渡期无法编译 @hide API，此处以反射调用；platform 签名的特权应用
 * 不受 hidden-API greylist 限制。步骤 5 进树后可替换为直调（同名同参，机械替换）。
 *
 * 所需权限（步骤 6 privapp 白名单）：
 * - INJECT_EVENTS（input 注入系）
 * - FORCE_STOP_PACKAGES（am force-stop 等价）
 */
object PrivilegedSystemApi {
    private const val TAG = "PrivilegedSystemApi"

    /** InputManager.injectInputEvent 的 mode 常量：异步注入（不等待分发完成） */
    private const val INJECT_INPUT_EVENT_MODE_ASYNC = 0

    /**
     * InputEvent.setDisplayId(@hide)：指定注入目标显示器。
     * 步骤 5 进树后替换为直调（同名同参）。
     */
    private val setDisplayIdMethod by lazy {
        InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType).apply {
            isAccessible = true
        }
    }

    /** 为输入事件标记目标显示器，返回同一事件 */
    fun withDisplayId(event: InputEvent, displayId: Int): InputEvent {
        return try {
            setDisplayIdMethod.invoke(event, displayId)
            event
        } catch (e: Exception) {
            Log.e(TAG, "setDisplayId($displayId) failed: ${e.cause ?: e}")
            event
        }
    }

    private val injectInputEventMethod by lazy {
        // injectInputEvent(InputEvent, int) 为 @hide SystemApi；InputManager 实例经 getSystemService public 通道获取
        android.hardware.input.InputManager::class.java.getMethod(
            "injectInputEvent",
            InputEvent::class.java,
            Int::class.javaPrimitiveType
        ).apply { isAccessible = true }
    }

    private fun inputManager(): android.hardware.input.InputManager =
        INPUT_MANAGER_CLASS.getDeclaredMethod("getInstance").invoke(null) as android.hardware.input.InputManager

    private val INPUT_MANAGER_CLASS = android.hardware.input.InputManager::class.java

    /** 注入一个输入事件（MotionEvent/KeyEvent），成功返回 true */
    fun injectInputEvent(event: InputEvent): Boolean {
        return try {
            injectInputEventMethod.invoke(inputManager(), event, INJECT_INPUT_EVENT_MODE_ASYNC) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, "injectInputEvent failed: ${e.cause ?: e}")
            false
        }
    }

    /** 构造并注入一次坐标点击（down + up），注入目标为指定显示器 */
    fun injectTapOnDisplay(x: Int, y: Int, displayId: Int): Boolean {
        val now = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
        val up = MotionEvent.obtain(now, now + 50, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(), 0)
        val okDown = injectInputEvent(withDisplayId(down, displayId))
        val okUp = injectInputEvent(withDisplayId(up, displayId))
        down.recycle()
        up.recycle()
        return okDown && okUp
    }

    /** 注入目标为焦点显示屏（默认显示器） */
    fun injectTap(x: Int, y: Int): Boolean = injectTapOnDisplay(x, y, Display.DEFAULT_DISPLAY)

    /** 构造并注入长按（down + duration 毫秒 + up）到指定显示器 */
    fun injectLongPressOnDisplay(x: Int, y: Int, durationMs: Long, displayId: Int): Boolean {
        val downTime = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
        injectInputEvent(withDisplayId(down, displayId))
        Thread.sleep(durationMs.coerceIn(300L, 5000L))
        val up = MotionEvent.obtain(downTime, downTime + durationMs, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(), 0)
        val ok = injectInputEvent(withDisplayId(up, displayId))
        down.recycle()
        up.recycle()
        return ok
    }

    fun injectLongPress(x: Int, y: Int, durationMs: Long): Boolean =
        injectLongPressOnDisplay(x, y, durationMs, Display.DEFAULT_DISPLAY)

    /** 构造并注入滑动手势（多点路径插值 move 事件）到指定显示器 */
    fun injectSwipeOnDisplay(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        durationMs: Long,
        displayId: Int
    ): Boolean {
        val downTime = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, startX.toFloat(), startY.toFloat(), 0)
        var ok = injectInputEvent(withDisplayId(down, displayId))

        val steps = (durationMs / 16L).coerceIn(2, 120).toInt()
        val dx = (endX - startX).toFloat() / steps
        val dy = (endY - startY).toFloat() / steps
        for (i in 1 until steps) {
            val t = downTime + durationMs * i / steps
            val move = MotionEvent.obtain(
                downTime, t, MotionEvent.ACTION_MOVE,
                startX + dx * i, startY + dy * i, 0
            )
            ok = ok and injectInputEvent(withDisplayId(move, displayId))
            move.recycle()
        }
        val up = MotionEvent.obtain(downTime, downTime + durationMs, MotionEvent.ACTION_UP, endX.toFloat(), endY.toFloat(), 0)
        ok = ok and injectInputEvent(withDisplayId(up, displayId))
        down.recycle()
        up.recycle()
        return ok
    }

    fun injectSwipe(startX: Int, startY: Int, endX: Int, endY: Int, durationMs: Long): Boolean =
        injectSwipeOnDisplay(startX, startY, endX, endY, durationMs, Display.DEFAULT_DISPLAY)

    /** 构造并注入一次按键（down + up，可携带 meta）到指定显示器 */
    fun injectKeyEventOnDisplay(keyCode: Int, metaState: Int, displayId: Int): Boolean {
        val now = android.os.SystemClock.uptimeMillis()
        val down = KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
        val up = KeyEvent(now, now + 20, KeyEvent.ACTION_UP, keyCode, 0, metaState)
        val ok = injectInputEvent(withDisplayId(down, displayId)) &&
            injectInputEvent(withDisplayId(up, displayId))
        return ok
    }

    /** 注入一次按键（down + up） */
    fun injectKey(keyCode: Int): Boolean = injectKeyEventOnDisplay(keyCode, 0, Display.DEFAULT_DISPLAY)

    /**
     * 字符序列注入（副屏静默输入）：ACTION_MULTIPLE KeyEvent 承载字符块，
     * TextView 系编辑控件直接插入，无剪贴板/软键盘依赖；分批防单事件过大。
     */
    fun injectCharactersOnDisplay(text: String, displayId: Int): Boolean {
        if (text.isEmpty()) return true
        val now = android.os.SystemClock.uptimeMillis()
        var ok = true
        text.chunked(64).forEach { chunk ->
            val event = KeyEvent(now, chunk, 0, 0)
            ok = ok and injectInputEvent(withDisplayId(event, displayId))
        }
        return ok
    }

    /**
     * 按整份 MotionEvent 参数构造并注入单事件到指定显示器（悬浮窗触控转发用，
     * 保留 downTime/eventTime/pressure/precision 等全部语义）。
     */
    fun injectMotionEventOnDisplay(
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
        edgeFlags: Int,
        displayId: Int
    ): Boolean {
        val event = MotionEvent.obtain(
            downTime, eventTime, action, x, y, pressure, size,
            metaState, xPrecision, yPrecision, deviceId, edgeFlags
        )
        val ok = injectInputEvent(withDisplayId(event, displayId))
        event.recycle()
        return ok
    }

    /**
     * 强制停止应用（ActivityManager.forceStopPackage，@hide，需 FORCE_STOP_PACKAGES）。
     * 成功返回 true；权限缺失或包名无效抛出的异常转译为 false + 日志。
     */
    fun forceStopPackage(context: Context, packageName: String): Boolean {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.javaClass
                .getMethod("forceStopPackage", String::class.java)
                .apply { isAccessible = true }
                .invoke(am, packageName)
            true
        } catch (e: Exception) {
            Log.e(TAG, "forceStopPackage($packageName) failed: ${e.cause ?: e}")
            false
        }
    }
}
