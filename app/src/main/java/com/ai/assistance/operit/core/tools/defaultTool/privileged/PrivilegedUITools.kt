package com.ai.assistance.operit.core.tools.defaultTool.privileged

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.KeyEvent
import com.ai.assistance.operit.core.tools.defaultTool.accessbility.AccessibilityUITools
import com.ai.assistance.operit.core.tools.system.privileged.PrivilegedSystemApi
import com.ai.assistance.operit.data.model.ToolResult
import com.ai.assistance.operit.core.tools.ToolInterface
import com.ai.assistance.operit.core.tools.AITool
import com.ai.assistance.operit.data.model.StringResultData
import com.ai.assistance.operit.data.model.UIActionResultData
import com.ai.assistance.operit.util.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PRIVILEGED 档 UI 工具：INJECT_EVENTS 直注入（InputManager 反射通路）。
 *
 * 坐标系注入系动作不走无障碍通道；元素级操作（clickElement/getPageInfo 等）
 * 继承无障碍实现——节点树读取本就依赖 AccessibilityService。
 */
open class PrivilegedUITools(context: Context) : AccessibilityUITools(context) {

    companion object {
        private const val TAG = "PrivilegedUITools"
    }

    override suspend fun tap(tool: AITool): ToolResult {
        val x = tool.parameters.find { it.name == "x" }?.value?.toIntOrNull()
        val y = tool.parameters.find { it.name == "y" }?.value?.toIntOrNull()
        if (x == null || y == null) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Missing or invalid coordinates. Both 'x' and 'y' must be valid integers."
            )
        }
        return try {
            withContext(Dispatchers.Main) { operationOverlay.hideImmediately() }
            val ok = PrivilegedSystemApi.injectTap(x, y)
            if (ok) {
                withContext(Dispatchers.Main) {
                    operationOverlay.showTap(x, y)
                    operationOverlay.hide()
                }
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = UIActionResultData(
                        actionType = "tap",
                        actionDescription = "Successfully tapped at coordinates ($x, $y) via input injection",
                        coordinates = Pair(x, y)
                    ),
                    error = ""
                )
            } else {
                operationOverlay.hide()
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Failed to inject tap: INJECT_EVENTS permission rejected or injection unavailable."
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error injecting tap", e)
            operationOverlay.hide()
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error injecting tap: ${e.message}"
            )
        }
    }

    override suspend fun longPress(tool: AITool): ToolResult {
        val x = tool.parameters.find { it.name == "x" }?.value?.toIntOrNull()
        val y = tool.parameters.find { it.name == "y" }?.value?.toIntOrNull()
        val duration = tool.parameters.find { it.name == "duration" }?.value?.toLongOrNull() ?: 1000L
        if (x == null || y == null) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Missing or invalid coordinates. Both 'x' and 'y' must be valid integers."
            )
        }
        return try {
            withContext(Dispatchers.Main) { operationOverlay.hideImmediately() }
            val ok = withContext(Dispatchers.IO) {
                PrivilegedSystemApi.injectLongPress(x, y, duration)
            }
            if (ok) {
                withContext(Dispatchers.Main) {
                    operationOverlay.showTap(x, y)
                    operationOverlay.hide()
                }
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = UIActionResultData(
                        actionType = "longPress",
                        actionDescription = "Successfully long-pressed at ($x, $y) for ${duration}ms via input injection",
                        coordinates = Pair(x, y)
                    ),
                    error = ""
                )
            } else {
                operationOverlay.hide()
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Failed to inject long press: INJECT_EVENTS permission rejected or injection unavailable."
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error injecting long press", e)
            operationOverlay.hide()
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error injecting long press: ${e.message}"
            )
        }
    }

    override suspend fun swipe(tool: AITool): ToolResult {
        val startX = tool.parameters.find { it.name == "start_x" }?.value?.toIntOrNull()
        val startY = tool.parameters.find { it.name == "start_y" }?.value?.toIntOrNull()
        val endX = tool.parameters.find { it.name == "end_x" }?.value?.toIntOrNull()
        val endY = tool.parameters.find { it.name == "end_y" }?.value?.toIntOrNull()
        val duration = tool.parameters.find { it.name == "duration" }?.value?.toLongOrNull() ?: 300L
        if (startX == null || startY == null || endX == null || endY == null) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Missing or invalid coordinates. start_x/start_y/end_x/end_y must be valid integers."
            )
        }
        return try {
            withContext(Dispatchers.Main) { operationOverlay.hideImmediately() }
            val ok = withContext(Dispatchers.IO) {
                PrivilegedSystemApi.injectSwipe(startX, startY, endX, endY, duration)
            }
            if (ok) {
                withContext(Dispatchers.Main) {
                    operationOverlay.showSwipe(startX, startY, endX, endY)
                    operationOverlay.hide()
                }
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = UIActionResultData(
                        actionType = "swipe",
                        actionDescription = "Successfully swiped from ($startX, $startY) to ($endX, $endY) via input injection",
                        coordinates = Pair(startX, startY)
                    ),
                    error = ""
                )
            } else {
                operationOverlay.hide()
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Failed to inject swipe: INJECT_EVENTS permission rejected or injection unavailable."
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error injecting swipe", e)
            operationOverlay.hide()
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error injecting swipe: ${e.message}"
            )
        }
    }

    override suspend fun pressKey(tool: AITool): ToolResult {
        val keyCodeRaw = tool.parameters.find { it.name == "key_code" }?.value
        if (keyCodeRaw == null) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Missing 'key_code' parameter."
            )
        }
        val keyCode = parseKeyCode(keyCodeRaw)
        if (keyCode == null) {
            return ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Unknown key code: $keyCodeRaw"
            )
        }
        return try {
            if (PrivilegedSystemApi.injectKey(keyCode)) {
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = UIActionResultData(
                        actionType = "pressKey",
                        actionDescription = "Successfully pressed key $keyCodeRaw via input injection",
                        error = ""
                    ),
                    error = ""
                )
            } else {
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Failed to inject key event: INJECT_EVENTS permission rejected or injection unavailable."
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error injecting key press", e)
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error injecting key press: ${e.message}"
            )
        }
    }

    override suspend fun setInputText(tool: AITool): ToolResult {
        val text = tool.parameters.find { it.name == "text" }?.value ?: ""
        return try {
            withContext(Dispatchers.Main) { operationOverlay.hideImmediately() }

            // 清空目标输入框后经剪贴板粘贴注入（platform 剪贴板可后台写）
            PrivilegedSystemApi.injectKey(KeyEvent.KEYCODE_CLEAR)
            if (text.isEmpty()) {
                withContext(Dispatchers.Main) { operationOverlay.hide() }
                return ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = UIActionResultData(
                        actionType = "textInput",
                        actionDescription = "Successfully cleared input field",
                        error = ""
                    ),
                    error = ""
                )
            }
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("operit_input", text))
            val pasted = PrivilegedSystemApi.injectKey(KeyEvent.KEYCODE_PASTE)
            withContext(Dispatchers.Main) { operationOverlay.hide() }
            if (pasted) {
                ToolResult(
                    toolName = tool.name,
                    success = true,
                    result = UIActionResultData(
                        actionType = "textInput",
                        actionDescription = "Successfully set input text to: $text via clipboard paste injection",
                        error = ""
                    ),
                    error = ""
                )
            } else {
                ToolResult(
                    toolName = tool.name,
                    success = false,
                    result = StringResultData(""),
                    error = "Failed to inject paste key event: INJECT_EVENTS permission rejected."
                )
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error setting input text", e)
            operationOverlay.hide()
            ToolResult(
                toolName = tool.name,
                success = false,
                result = StringResultData(""),
                error = "Error setting input text: ${e.message}"
            )
        }
    }

    /** key_code 参数解析：数字串直接用，KEYCODE_XXX 命名经 KeyEvent 键名表转换 */
    private fun parseKeyCode(raw: String): Int? {
        raw.toIntOrNull()?.let { return it }
        return try {
            val field = KeyEvent::class.java.getField(raw.trim())
            field.getInt(null)
        } catch (e: NoSuchFieldException) {
            null
        }
    }
}
