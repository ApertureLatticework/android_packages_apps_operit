package com.ai.assistance.operit.core.tools.system.privileged

import android.content.Context
import android.util.Log
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

    private val injectInputEventMethod by lazy {
        // InputManager.getInstance() 为 public API；injectInputEvent(InputEvent, int) 为 @hide SystemApi
        val instance = android.hardware.input.InputManager.getInstance()
        instance.javaClass.getMethod(
            "injectInputEvent",
            InputEvent::class.java,
            Int::class.javaPrimitiveType
        ).apply { isAccessible = true }
    }

    /** 注入一个输入事件（MotionEvent/KeyEvent），成功返回 true */
    fun injectInputEvent(event: InputEvent): Boolean {
        return try {
            val instance = android.hardware.input.InputManager.getInstance()
            injectInputEventMethod.invoke(instance, event, INJECT_INPUT_EVENT_MODE_ASYNC) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, "injectInputEvent failed: ${e.cause ?: e}")
            false
        }
    }

    /** 构造并注入一次坐标点击（down + up） */
    fun injectTap(x: Int, y: Int, displayId: Int = android.view.Display.DEFAULT_DISPLAY): Boolean {
        val now = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
            .apply { displayId(displayId) }
        val up = MotionEvent.obtain(now, now + 50, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(), 0)
            .apply { displayId(displayId) }
        val okDown = injectInputEvent(down)
        val okUp = injectInputEvent(up)
        down.recycle()
        up.recycle()
        return okDown && okUp
    }

    /** 构造并注入长按（down + duration 毫秒 + up） */
    fun injectLongPress(x: Int, y: Int, durationMs: Long): Boolean {
        val downTime = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
        injectInputEvent(down)
        Thread.sleep(durationMs.coerceIn(300L, 5000L))
        val up = MotionEvent.obtain(downTime, downTime + durationMs, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(), 0)
        val ok = injectInputEvent(up)
        down.recycle()
        up.recycle()
        return ok
    }

    /** 构造并注入滑动手势（多点路径插值 move 事件） */
    fun injectSwipe(startX: Int, startY: Int, endX: Int, endY: Int, durationMs: Long): Boolean {
        val downTime = android.os.SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, startX.toFloat(), startY.toFloat(), 0)
        var ok = injectInputEvent(down)

        val steps = (durationMs / 16L).coerceIn(2, 120).toInt()
        val dx = (endX - startX).toFloat() / steps
        val dy = (endY - startY).toFloat() / steps
        for (i in 1 until steps) {
            val t = downTime + durationMs * i / steps
            val move = MotionEvent.obtain(
                downTime, t, MotionEvent.ACTION_MOVE,
                startX + dx * i, startY + dy * i, 0
            )
            ok = ok and injectInputEvent(move)
            move.recycle()
        }
        val up = MotionEvent.obtain(downTime, downTime + durationMs, MotionEvent.ACTION_UP, endX.toFloat(), endY.toFloat(), 0)
        ok = ok and injectInputEvent(up)
        down.recycle()
        up.recycle()
        return ok
    }

    /** 构造并注入一次按键（down + up） */
    fun injectKey(keyCode: Int): Boolean {
        val now = android.os.SystemClock.uptimeMillis()
        val down = KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0)
        val up = KeyEvent(now, now + 20, KeyEvent.ACTION_UP, keyCode, 0)
        val ok = injectInputEvent(down) && injectInputEvent(up)
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
