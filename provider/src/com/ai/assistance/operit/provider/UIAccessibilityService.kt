package com.ai.assistance.operit.provider

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.hypot

/**
 * 无障碍语义与执行服务。
 *
 * 与主应用的契约即 [IAccessibilityProvider] 十方法：语义树以 uiautomator 风格 XML
 * 序列化（nodeId 为节点对象身份哈希，进程内稳定，findFocusedNodeId/setTextOnNode
 * 同源消费）；点击/长按/滑走为 GestureDescription 坐标手势；截图走
 * AccessibilityService.takeScreenshot（API 30+，安全页面对 priv 场景生效）。
 */
class UIAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "UIAccessibilityService"
        private const val CLICK_DURATION_MS = 50L
        private const val LONG_PRESS_DURATION_MS = 600L
        private const val SCREENSHOT_TIMEOUT_MS = 3000L
        private const val MAX_HIERARCHY_DEPTH = 50
    }

    /** 最近一次窗口切换的组件名，作为 getCurrentActivityName 的数据源 */
    @Volatile
    private var currentActivityName: String? = null

    private val binder = object : IAccessibilityProvider.Stub() {

        override fun getUiHierarchy(): String {
            return serializeHierarchy()
        }

        override fun performClick(x: Int, y: Int): Boolean {
            return dispatchTap(x.toFloat(), y.toFloat(), CLICK_DURATION_MS)
        }

        override fun performLongPress(x: Int, y: Int): Boolean {
            return dispatchTap(x.toFloat(), y.toFloat(), LONG_PRESS_DURATION_MS)
        }

        override fun performGlobalAction(actionId: Int): Boolean {
            return this@UIAccessibilityService.performGlobalAction(actionId)
        }

        override fun performSwipe(
            startX: Int,
            startY: Int,
            endX: Int,
            endY: Int,
            duration: Long
        ): Boolean {
            return dispatchSwipe(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), duration)
        }

        override fun findFocusedNodeId(): String {
            val focused = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            return focused?.let { nodeIdOf(it) } ?: ""
        }

        override fun setTextOnNode(nodeId: String, text: String): Boolean {
            val node = findNodeById(rootInActiveWindow, nodeId) ?: return false
            val arguments = android.os.Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
            }
            return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }

        override fun takeScreenshot(path: String, format: String): Boolean {
            return this@UIAccessibilityService.takeScreenshotToFile(path, format)
        }

        override fun isAccessibilityServiceEnabled(): Boolean {
            // 服务已连接并收到 Binder 调用，无障碍链路必然在线
            return true
        }

        override fun getCurrentActivityName(): String {
            return currentActivityName ?: ""
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        RemoteBinderService.attach(binder)
        Log.i(TAG, "Accessibility provider connected")
    }

    override fun onUnbind(intent: Intent): Boolean {
        RemoteBinderService.detach()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        RemoteBinderService.detach()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val name = event.className?.toString()
            if (!name.isNullOrBlank()) {
                currentActivityName = name
            }
        }
    }

    override fun onInterrupt() {
        // 无长音频反馈通道，无需处理
    }

    // ==================== 序列化 ====================

    private fun nodeIdOf(node: AccessibilityNodeInfo): String =
        Integer.toHexString(System.identityHashCode(node))

    private fun serializeHierarchy(): String {
        val root = rootInActiveWindow ?: run {
            // 多窗口场景：主窗口无内容时遍历 attached windows 取第一个可用根
            windows.firstOrNull { it.root != null }?.root
        }
        if (root == null) return ""
        val builder = StringBuilder(64 * 1024)
        builder.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n")
        builder.append("<hierarchy rotation=\"0\">\n")
        appendNode(builder, root, 0)
        builder.append("\n</hierarchy>")
        return builder.toString()
    }

    private fun appendNode(builder: StringBuilder, node: AccessibilityNodeInfo, depth: Int) {
        if (depth > MAX_HIERARCHY_DEPTH) return
        val indent = "    ".repeat(depth)
        builder.append(indent).append("<node")

        val bounds = Rect().also { node.getBoundsInScreen(it) }
        fun attr(name: String, value: String?) {
            val escaped = (value ?: "")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("\n", "\\n")
            builder.append(' ').append(name).append("=\"").append(escaped).append('"')
        }

        attr("index", depth.toString())
        attr("nodeId", nodeIdOf(node))
        attr("text", node.text?.toString())
        attr("resource-id", node.viewIdResourceName)
        attr("class", node.className?.toString())
        attr("package", node.packageName?.toString())
        attr("content-desc", node.contentDescription?.toString())
        attr("checkable", node.isCheckable.toString())
        attr("checked", node.isChecked.toString())
        attr("clickable", node.isClickable.toString())
        attr("enabled", node.isEnabled.toString())
        attr("focusable", node.isFocusable.toString())
        attr("focused", node.isFocused.toString())
        attr("scrollable", node.isScrollable.toString())
        attr("long-clickable", node.isLongClickable.toString())
        attr("password", node.isPassword.toString())
        attr("selected", node.isSelected.toString())
        attr("visible-to-user", node.isVisibleToUser.toString())
        builder.append(" bounds=\"").append(bounds.toShortString()).append('"')

        val childCount = node.childCount
        if (childCount <= 0) {
            builder.append(" />")
            return
        }
        builder.append(">\n")
        for (i in 0 until childCount) {
            val child = node.getChild(i) ?: continue
            try {
                appendNode(builder, child, depth + 1)
                builder.append('\n')
            } finally {
                child.recycle()
            }
        }
        builder.append(indent).append("</node>")
    }

    private fun findNodeById(root: AccessibilityNodeInfo?, nodeId: String): AccessibilityNodeInfo? {
        if (root == null || nodeId.isBlank()) return null
        return findByNodeId(root, nodeId)
    }

    private fun findByNodeId(node: AccessibilityNodeInfo, nodeId: String): AccessibilityNodeInfo? {
        if (nodeIdOf(node) == nodeId) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                val hit = findByNodeId(child, nodeId)
                if (hit != null) return hit
            } finally {
                if (nodeIdOf(child) != nodeId) child.recycle()
            }
        }
        return null
    }

    // ==================== 手势 ====================

    private fun dispatchTap(x: Float, y: Float, duration: Long): Boolean {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0L, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture, null, null)
    }

    private fun dispatchSwipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long
    ): Boolean {
        // 与 uiautomator 惯例一致：极短距离以位移归一化路径，避免零长 Path
        val distance = hypot((endX - startX).toDouble(), (endY - startY).toDouble())
        val effectiveDuration = if (duration <= 0L) {
            (distance / 2.0).toLong().coerceIn(100L, 2000L)
        } else {
            duration
        }
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0L, effectiveDuration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture, null, null)
    }

    // ==================== 截图 ====================

    private fun takeScreenshotToFile(path: String, format: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Log.e(TAG, "takeScreenshot requires API 30+")
            return false
        }
        val latch = CountDownLatch(1)
        val resultHolder = arrayOfNulls<ScreenshotResult>(1)
        val errorHolder = arrayOfNulls<Int>(1)
        val callback = object : TakeScreenshotCallback {
            override fun onSuccess(screenshot: ScreenshotResult) {
                resultHolder[0] = screenshot
                latch.countDown()
            }

            override fun onFailure(errorCode: Int) {
                errorHolder[0] = errorCode
                latch.countDown()
            }
        }
        takeScreenshot(android.view.Display.DEFAULT_DISPLAY, mainExecutor, callback)
        if (!latch.await(SCREENSHOT_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            Log.e(TAG, "takeScreenshot timed out")
            return false
        }
        errorHolder[0]?.let {
            Log.e(TAG, "takeScreenshot failed with code $it")
            return false
        }
        val shot = resultHolder[0] ?: return false

        val bitmap = Bitmap.wrapHardwareBuffer(shot.hardwareBuffer, shot.colorSpace)
            ?: return false
        val usePng = format.equals("png", ignoreCase = true)
        val bitmapCopy = try {
            // 硬件位图不能直接编码，拷贝为软件位图
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } finally {
            bitmap.recycle()
        }
        return try {
            val output = File(path).apply { parentFile?.mkdirs() }
            FileOutputStream(output).use { stream ->
                if (usePng) {
                    bitmapCopy.compress(Bitmap.CompressFormat.PNG, 100, stream)
                } else {
                    bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "takeScreenshot write failed", e)
            false
        } finally {
            bitmapCopy.recycle()
            shot.hardwareBuffer.close()
        }
    }
}
