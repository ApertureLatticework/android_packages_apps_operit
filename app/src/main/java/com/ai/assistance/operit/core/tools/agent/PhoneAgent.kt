package com.ai.assistance.operit.core.tools.agent

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import androidx.core.content.FileProvider
import com.ai.assistance.operit.R
import com.ai.assistance.operit.api.chat.llmprovider.AIService
import com.ai.assistance.operit.core.chat.hooks.toPromptTurns
import com.ai.assistance.operit.core.tools.AIToolHandler
import com.ai.assistance.operit.core.tools.AppListData
import com.ai.assistance.operit.core.tools.defaultTool.standard.StandardUITools
import com.ai.assistance.operit.core.tools.system.AndroidPermissionLevel
import com.ai.assistance.operit.core.tools.system.live.LiveScreenMirror
import com.ai.assistance.operit.core.tools.system.privileged.PrivilegedSystemApi
import com.ai.assistance.operit.data.model.AITool
import com.ai.assistance.operit.data.model.ToolParameter
import com.ai.assistance.operit.data.model.ToolResult
import com.ai.assistance.operit.data.preferences.AndroidPermissionPreferences
import com.ai.assistance.operit.data.preferences.DisplayPreferencesManager
import com.ai.assistance.operit.data.preferences.androidPermissionPreferences
import com.ai.assistance.operit.services.FloatingChatService
import com.ai.assistance.operit.ui.common.displays.UIAutomationProgressOverlay
import com.ai.assistance.operit.ui.common.displays.VirtualDisplayOverlay
import com.ai.assistance.operit.util.AppLogger
import com.ai.assistance.operit.util.ImageOutputFormat
import com.ai.assistance.operit.util.ImagePoolManager
import com.ai.assistance.operit.util.ImageRegistrationOptions
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Configuration for the PhoneAgent. */
data class AgentConfig(
    val maxSteps: Int = 20
)

/** Result of a single agent step. */
data class StepResult(
    val success: Boolean,
    val finished: Boolean,
    val action: ParsedAgentAction?,
    val thinking: String?,
    val message: String? = null
)

/** Parsed action from the model's response. */
data class ParsedAgentAction(
    val metadata: String,
    val actionName: String?,
    val fields: Map<String, String>
)

private data class PrivilegedExecutionState(
    val isPrivilegedLevel: Boolean
)

private fun resolvePrivilegedExecutionState(
    context: Context,
    androidPermissionPreferences: AndroidPermissionPreferences,
    onExperimentalFlagReadError: ((Exception) -> Unit)? = null
): PrivilegedExecutionState {
    val preferredLevel = androidPermissionPreferences.getPreferredPermissionLevel()
        ?: AndroidPermissionLevel.STANDARD

    var isPrivilegedLevel = preferredLevel == AndroidPermissionLevel.PRIVILEGED

    if (isPrivilegedLevel) {
        val experimentalEnabled = try {
            DisplayPreferencesManager.getInstance(context).isExperimentalVirtualDisplayEnabled()
        } catch (e: Exception) {
            onExperimentalFlagReadError?.invoke(e)
            true
        }
        if (!experimentalEnabled) {
            isPrivilegedLevel = false
        }
    }

    return PrivilegedExecutionState(isPrivilegedLevel = isPrivilegedLevel)
}

/**
 * AI-powered agent for automating Android phone interactions.
 *
 * The agent uses a vision-language model to understand screen content
 * and decide on actions to complete user tasks.
 */
class PhoneAgent(
    private val context: Context,
    private val config: AgentConfig,
    private val uiService: AIService,
    private val actionHandler: ActionHandler,
    val agentId: String = "default",
    private val cleanupOnFinish: Boolean = (agentId != "default"),
) {
    private var _stepCount = 0
    val stepCount: Int
        get() = _stepCount

    private val _contextHistory = mutableListOf<Pair<String, String>>()
    val contextHistory: List<Pair<String, String>>
        get() = _contextHistory.toList()

    private var pauseFlow: StateFlow<Boolean>? = null

    private val requiresVirtualScreen: Boolean = agentId.isNotBlank() && agentId != "default"
    private val isMainScreenAgent: Boolean = agentId.isBlank() || agentId == "default"

    init {
        actionHandler.setAgentId(agentId)
    }

    private suspend fun awaitIfPaused() {
        val flow = pauseFlow ?: return
        if (!flow.value) {
            return
        }
        AppLogger.d("PhoneAgent", "[$agentId] awaitIfPaused: entering pause loop, delay starting")
        try {
            while (flow.value) {
                delay(200)
            }
        } finally {
            AppLogger.d("PhoneAgent", "[$agentId] awaitIfPaused: exiting pause loop")
        }
    }

    private fun hasNativeDisplay(logMessageSuffix: String): Boolean {
        if (isMainScreenAgent) return false
        return try {
            NativeVirtualDisplay.getDisplayId(agentId) != null || NativeVirtualDisplay.getVideoSize(agentId) != null
        } catch (e: Exception) {
            AppLogger.e("PhoneAgent", "[$agentId] $logMessageSuffix", e)
            false
        }
    }

    private fun shouldUseVirtualScreenUi(hasDisplay: Boolean): Boolean {
        // 副屏会话的代理 UI 挂原生副屏悬浮窗；主屏会话沿用常规主屏自动化观感
        return !isMainScreenAgent && hasDisplay
    }

    private suspend fun ensureRequiredVirtualScreenOrError(): String? {
        if (!requiresVirtualScreen) return null

        if (hasNativeDisplay("Error checking native display state before ensure")) {
            return null
        }

        val permissionState = resolvePrivilegedExecutionState(
            context = context,
            androidPermissionPreferences = androidPermissionPreferences
        )
        if (!permissionState.isPrivilegedLevel) {
            return context.getString(R.string.phone_agent_need_privileged_permission)
        }

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val dpi = metrics.densityDpi

        val session = try {
            NativeVirtualDisplay.ensureDisplay(context, agentId, width, height, dpi)
        } catch (e: Exception) {
            AppLogger.e("PhoneAgent", "[$agentId] ensureRequiredVirtualScreen: ensureDisplay failed", e)
            null
        }

        if (session == null) {
            return context.getString(R.string.phone_agent_virtual_screen_create_failed)
        }

        try {
            VirtualDisplayOverlay.getInstance(context, agentId).show(session.displayId)
        } catch (e: Exception) {
            AppLogger.e("PhoneAgent", "[$agentId] ensureRequiredVirtualScreen: error showing overlay", e)
        }

        return null
    }

    private suspend fun prewarmNativeDisplayIfNeeded(
        hasDisplayAtStart: Boolean,
        targetApp: String?
    ): Pair<Boolean, String?> {
        if (isMainScreenAgent) return Pair(false, null)
        if (hasDisplayAtStart) return Pair(true, null)
        val targetAppForPrewarm = targetApp?.takeIf { it.isNotBlank() } ?: return Pair(false, null)

        val permissionState = resolvePrivilegedExecutionState(
            context = context,
            androidPermissionPreferences = androidPermissionPreferences
        )
        if (!permissionState.isPrivilegedLevel) return Pair(false, null)

        AppLogger.d(
            "PhoneAgent",
            "[$agentId] run: prewarming native virtual display via Launch(app='$targetAppForPrewarm')"
        )
        val prewarmResult = try {
            actionHandler.executeAgentAction(
                ParsedAgentAction(
                    metadata = "do",
                    actionName = "Launch",
                    fields = mapOf(
                        "action" to "Launch",
                        "app" to targetAppForPrewarm
                    )
                )
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Pair(false, context.getString(R.string.phone_agent_virtual_screen_prewarm_failed, e.message ?: ""))
        }

        val hasDisplayAfterPrewarm = hasNativeDisplay("Error checking native display state after prewarm")
        if (!hasDisplayAfterPrewarm) {
            return Pair(false, prewarmResult.message ?: context.getString(R.string.phone_agent_virtual_screen_not_started))
        }

        return Pair(true, null)
    }

    private suspend fun prewarmMainScreenLaunchIfNeeded(targetApp: String?): String? {
        if (!isMainScreenAgent) return null
        val targetAppForPrewarm = targetApp?.takeIf { it.isNotBlank() } ?: return null

        AppLogger.d(
            "PhoneAgent",
            "[$agentId] run: prewarming main-screen launch via Launch(app='$targetAppForPrewarm')"
        )
        val prewarmResult = try {
            actionHandler.executeAgentAction(
                ParsedAgentAction(
                    metadata = "do",
                    actionName = "Launch",
                    fields = mapOf(
                        "action" to "Launch",
                        "app" to targetAppForPrewarm
                    )
                )
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return "Exception while prewarming main-screen app launch: ${e.message}"
        }

        return if (prewarmResult.success) {
            null
        } else {
            prewarmResult.message ?: "Failed to prewarm main-screen app launch"
        }
    }

    /**
     * Run the agent to complete a task.
     *
     * @param task Natural language description of the task.
     * @param systemPrompt System prompt for the UI automation agent.
     * @param onStep Optional callback invoked after each step with the StepResult.
     * @return Final message from the agent.
     */
    suspend fun run(
        task: String,
        systemPrompt: String,
        onStep: (suspend (StepResult) -> Unit)? = null,
        isPausedFlow: StateFlow<Boolean>? = null,
        targetApp: String? = null
    ): String {
        val floatingService = FloatingChatService.getInstance()
        val job = currentCoroutineContext()[Job]

        if (job != null) {
            PhoneAgentJobRegistry.register(agentId, job)
        } else {
            AppLogger.w("PhoneAgent", "[$agentId] run: no Job in coroutineContext, registry disabled")
        }

        val requiredVirtualScreenError = ensureRequiredVirtualScreenOrError()
        if (requiredVirtualScreenError != null) {
            return requiredVirtualScreenError
        }

        // Live 主屏观察通道（步骤 6）：特权档主屏会话整程持有镜像引用，
        // 采集免弹窗；副屏会话的镜像面归步骤 7 原生副屏后端
        val holdLiveMirror = isMainScreenAgent && resolvePrivilegedExecutionState(
            context = context,
            androidPermissionPreferences = androidPermissionPreferences
        ).isPrivilegedLevel
        if (holdLiveMirror && !LiveScreenMirror.acquire(context)) {
            return context.getString(R.string.live_capture_unavailable)
        }
        if (holdLiveMirror) {
            // 帧泵会话（live_pipeline_completion 步骤 2）：指示器与进度遮罩常隐藏，
            // 避免泵内帧被悬浮窗污染；恢复在 finally 统一处理
            FloatingChatService.getInstance()?.setStatusIndicatorVisible(false)
            UIAutomationProgressOverlay.getInstance(context).setOverlayVisible(false)
        }

        if (isMainScreenAgent) {
            val mainScreenPrewarmError = prewarmMainScreenLaunchIfNeeded(targetApp)
            if (mainScreenPrewarmError != null) {
                return mainScreenPrewarmError
            }
        }

        var hasDisplayAtStart = hasNativeDisplay("Error checking native virtual display state")
        val (prewarmedDisplay, prewarmError) = prewarmNativeDisplayIfNeeded(hasDisplayAtStart, targetApp)
        if (prewarmError != null) {
            return prewarmError
        }
        hasDisplayAtStart = prewarmedDisplay

        var useVirtualScreenUi = shouldUseVirtualScreenUi(hasDisplayAtStart)
        val progressOverlay = UIAutomationProgressOverlay.getInstance(context)
        var displayOverlay: VirtualDisplayOverlay? = if (useVirtualScreenUi) try {
            VirtualDisplayOverlay.getInstance(context, agentId)
        } catch (e: Exception) {
            AppLogger.e("PhoneAgent", "[$agentId] Error getting VirtualDisplayOverlay instance", e)
            null
        } else null

        val pausedMutable = isPausedFlow as? MutableStateFlow<Boolean>

        try {
            // Setup UI for agent run: hide window, then choose indicator based on whether the native virtual display is active
            floatingService?.setFloatingWindowVisible(false)
            if (useVirtualScreenUi) {
                useVirtualScreenIndicatorForAgent(context, agentId)
            } else {
                useFullscreenStatusIndicatorForAgent(context, agentId)
            }
            if (useVirtualScreenUi) {
                displayOverlay?.showAutomationControls(
                    totalSteps = config.maxSteps,
                    initialStatus = context.getString(R.string.phone_agent_thinking),
                    onTogglePauseResume = { isPaused -> pausedMutable?.value = isPaused },
                    onExit = {
                        PhoneAgentJobRegistry.cancelAgent(agentId, "User cancelled UI automation")
                        job?.cancel(CancellationException("User cancelled UI automation"))
                    }
                )
            } else {
                progressOverlay.show(
                    config.maxSteps,
                    context.getString(R.string.phone_agent_thinking),
                    onCancel = {
                        PhoneAgentJobRegistry.cancelAgent(agentId, "User cancelled UI automation")
                        job?.cancel(CancellationException("User cancelled UI automation"))
                    },
                    onToggleTakeOver = { isPaused -> pausedMutable?.value = isPaused }
                )
            }

            reset()
            _contextHistory.add("system" to systemPrompt)
            pauseFlow = isPausedFlow

            // First step with user prompt
            AppLogger.d("PhoneAgent", "[$agentId] run: starting first step for task='$task', hasDisplayAtStart=$hasDisplayAtStart")
            awaitIfPaused()
            var result = _executeStep(task, isFirst = true)
            val firstAction = result.action
            val firstStatusText = when {
                result.finished -> result.message ?: context.getString(R.string.phone_agent_completed)
                firstAction != null && firstAction.metadata == "do" -> {
                    val actionName = firstAction.actionName ?: ""
                    if (actionName.isNotEmpty()) context.getString(R.string.phone_agent_executing_action, actionName) else context.getString(R.string.phone_agent_executing)
                }
                else -> context.getString(R.string.phone_agent_thinking)
            }

            if (!useVirtualScreenUi) {
                val hasDisplayNow = hasNativeDisplay("Error re-checking native virtual display state after first step")

                if (shouldUseVirtualScreenUi(hasDisplayNow)) {
                    useVirtualScreenUi = true
                    try {
                        progressOverlay.hide()
                    } catch (_: Exception) {
                    }

                    try {
                        displayOverlay = VirtualDisplayOverlay.getInstance(context, agentId)
                    } catch (e: Exception) {
                        AppLogger.e("PhoneAgent", "[$agentId] Error getting VirtualDisplayOverlay instance when switching (first step)", e)
                        displayOverlay = null
                    }

                    if (displayOverlay != null) {
                        useVirtualScreenIndicatorForAgent(context, agentId)
                        displayOverlay?.showAutomationControls(
                            totalSteps = config.maxSteps,
                            initialStatus = firstStatusText,
                            onTogglePauseResume = { isPaused -> pausedMutable?.value = isPaused },
                            onExit = {
                                PhoneAgentJobRegistry.cancelAgent(agentId, "User cancelled UI automation")
                                job?.cancel(CancellationException("User cancelled UI automation"))
                            }
                        )
                        displayOverlay?.updateAutomationProgress(stepCount, config.maxSteps, firstStatusText)
                    } else {
                        progressOverlay.show(
                            config.maxSteps,
                            "Thinking...",
                            onCancel = {
                                PhoneAgentJobRegistry.cancelAgent(agentId, "User cancelled UI automation")
                                job?.cancel(CancellationException("User cancelled UI automation"))
                            },
                            onToggleTakeOver = { isPaused -> pausedMutable?.value = isPaused }
                        )
                        progressOverlay.updateProgress(stepCount, config.maxSteps, firstStatusText)
                        useVirtualScreenUi = false
                    }
                } else {
                    progressOverlay.updateProgress(stepCount, config.maxSteps, firstStatusText)
                }
            } else {
                displayOverlay?.updateAutomationProgress(stepCount, config.maxSteps, firstStatusText)
            }

            onStep?.invoke(result)

            if (result.finished) {
                return result.message ?: "Task completed"
            }

            // Continue until finished or max steps reached
            while (_stepCount < config.maxSteps) {
                awaitIfPaused()
                result = _executeStep(null, isFirst = false)
                val action = result.action
                val statusText = when {
                    result.finished -> result.message ?: context.getString(R.string.phone_agent_completed)
                    action != null && action.metadata == "do" -> {
                        val actionName = action.actionName ?: ""
                        if (actionName.isNotEmpty()) context.getString(R.string.phone_agent_executing_action, actionName) else context.getString(R.string.phone_agent_executing)
                    }
                    else -> context.getString(R.string.phone_agent_thinking)
                }

                if (!useVirtualScreenUi) {
                    val hasDisplayNow = hasNativeDisplay("Error re-checking native display state in loop")

                    if (shouldUseVirtualScreenUi(hasDisplayNow)) {
                        useVirtualScreenUi = true
                        progressOverlay.hide()
                        displayOverlay = VirtualDisplayOverlay.getInstance(context, agentId)
                        if (displayOverlay != null) {
                            useVirtualScreenIndicatorForAgent(context, agentId)
                            displayOverlay?.showAutomationControls(
                                totalSteps = config.maxSteps,
                                initialStatus = statusText,
                                onTogglePauseResume = { isPaused -> pausedMutable?.value = isPaused },
                                onExit = {
                                    PhoneAgentJobRegistry.cancelAgent(agentId, "User cancelled UI automation")
                                    job?.cancel(CancellationException("User cancelled UI automation"))
                                }
                            )
                            displayOverlay?.updateAutomationProgress(stepCount, config.maxSteps, statusText)
                        } else {
                            progressOverlay.show(
                                config.maxSteps,
                                "Thinking...",
                                onCancel = {
                                    PhoneAgentJobRegistry.cancelAgent(agentId, "User cancelled UI automation")
                                    job?.cancel(CancellationException("User cancelled UI automation"))
                                },
                                onToggleTakeOver = { isPaused -> pausedMutable?.value = isPaused }
                            )
                            progressOverlay.updateProgress(stepCount, config.maxSteps, statusText)
                            useVirtualScreenUi = false
                        }
                    } else {
                        progressOverlay.updateProgress(stepCount, config.maxSteps, statusText)
                    }
                } else {
                    displayOverlay?.updateAutomationProgress(stepCount, config.maxSteps, statusText)
                }

                onStep?.invoke(result)

                if (result.finished) {
                    return result.message ?: "Task completed"
                }
            }

            return "Max steps reached"
        } finally {
            AppLogger.d("PhoneAgent", "[$agentId] run: finishing, restoring UI")
            pauseFlow = null
            if (holdLiveMirror) {
                LiveScreenMirror.release()
                actionHandler.resetFrameSession()
            }
            floatingService?.setFloatingWindowVisible(true)
            if (isMainScreenAgent) {
                floatingService?.setStatusIndicatorVisible(false)
            } else {
                clearAgentIndicators(context, agentId)
            }
            if (useVirtualScreenUi) {
                displayOverlay?.hideAutomationControls()
            } else {
                progressOverlay.hide()
            }
            if (cleanupOnFinish) {
                AppLogger.d("PhoneAgent", "[$agentId] run: cleaning up agent session")
                try {
                    VirtualDisplayOverlay.hide(agentId)
                } catch (_: Exception) {
                }
                try {
                    NativeVirtualDisplay.shutdown(agentId)
                } catch (_: Exception) {
                }
            }
        }
    }

    /** Reset the agent state for a new task. */
    fun reset() {
        _contextHistory.clear()
        _stepCount = 0
        actionHandler.resetFrameSession()
    }

    /** Execute a single step of the agent loop. */
    private suspend fun _executeStep(userPrompt: String?, isFirst: Boolean): StepResult {
        _stepCount++
        AppLogger.d("PhoneAgent", "[$agentId] _executeStep: begin, step=$_stepCount")

        val screenshotLink = actionHandler.captureScreenshotForAgent(_contextHistory)
        val screenInfo = buildString {
            if (screenshotLink != null) {
                if (actionHandler.screenUnchangedSinceLastStep) {
                    appendLine("[SCREENSHOT] Screen unchanged since last step, reusing the same image:")
                } else {
                    appendLine("[SCREENSHOT] Below is the latest screen image:")
                }
                appendLine(screenshotLink)
            } else {
                appendLine("No screenshot available for this step.")
            }
        }.trim()

        val userMessage = if (isFirst) {
            "$userPrompt\n\n$screenInfo"
        } else {
            "** Screen Info **\n\n$screenInfo"
        }

        _contextHistory.add("user" to userMessage)

        val responseStream = uiService.sendMessage(
            context = context,
            chatHistory = _contextHistory.toList().toPromptTurns(),
            enableThinking = false,
            stream = true,
            preserveThinkInHistory = true
        )

        val contentBuilder = StringBuilder()
        responseStream.collect { chunk -> contentBuilder.append(chunk) }
        val fullResponse = contentBuilder.toString().trim()
        AppLogger.d("PhoneAgent", "[$agentId] _executeStep: AI response collected, length=${fullResponse.length}")

        val (thinking, answer) = parseThinkingAndAction(fullResponse)
        val historyEntry = "<think>$thinking</think><answer>$answer</answer>"
        _contextHistory.add("assistant" to historyEntry)

        val parsedAction = parseAgentAction(answer)

        if (parsedAction.metadata == "finish") {
            val message = parsedAction.fields["message"] ?: "Task finished."
            return StepResult(success = true, finished = true, action = parsedAction, thinking = thinking, message = message)
        }

        if (parsedAction.metadata == "do") {
            awaitIfPaused()
            val execResult = actionHandler.executeAgentAction(parsedAction)
            if (execResult.shouldFinish) {
                 return StepResult(success = execResult.success, finished = true, action = parsedAction, thinking = thinking, message = execResult.message)
            }
            return StepResult(success = execResult.success, finished = false, action = parsedAction, thinking = thinking, message = execResult.message)
        }

        val errorMessage = "Unknown action format: ${parsedAction.metadata}"
        return StepResult(success = false, finished = true, action = parsedAction, thinking = thinking, message = errorMessage)
    }

    private fun extractTagContent(text: String, tag: String): String? {
        val pattern = Regex("""<$tag>(.*?)</$tag>""", RegexOption.DOT_MATCHES_ALL)
        return pattern.find(text)?.groupValues?.getOrNull(1)?.trim()
    }

    private fun parseThinkingAndAction(content: String): Pair<String?, String> {
        val full = content.trim()
        val finishMarker = "finish(message="
        val finishIndex = full.indexOf(finishMarker)
        if (finishIndex >= 0) {
            val thinking = full.substring(0, finishIndex).trim().ifEmpty { null }
            val action = full.substring(finishIndex).trim()
            return thinking to action
        }
        val doMarker = "do(action="
        val doIndex = full.indexOf(doMarker)
        if (doIndex >= 0) {
            val thinking = full.substring(0, doIndex).trim().ifEmpty { null }
            val action = full.substring(doIndex).trim()
            return thinking to action
        }
        val thinkTag = extractTagContent(full, "think")
        val answerTag = extractTagContent(full, "answer")
        if (thinkTag != null || answerTag != null) {
            return thinkTag to (answerTag ?: full)
        }
        return null to full
    }

    private fun parseAgentAction(raw: String): ParsedAgentAction {
        val original = raw.trim()
        val finishIndex = original.lastIndexOf("finish(")
        val doIndex = original.lastIndexOf("do(")
        val startIndex = when {
            finishIndex >= 0 && doIndex >= 0 -> maxOf(finishIndex, doIndex)
            finishIndex >= 0 -> finishIndex
            doIndex >= 0 -> doIndex
            else -> -1
        }

        val trimmed = if (startIndex >= 0) original.substring(startIndex).trim() else original

        if (trimmed.startsWith("finish")) {
            val messageRegex = Regex("""finish\s*\(\s*message\s*=\s*\"(.*)\"\s*\)""", RegexOption.DOT_MATCHES_ALL)
            val message = messageRegex.find(trimmed)?.groupValues?.getOrNull(1) ?: ""
            return ParsedAgentAction(metadata = "finish", actionName = null, fields = mapOf("message" to message))
        }

        if (!trimmed.startsWith("do")) {
            return ParsedAgentAction(metadata = "unknown", actionName = null, fields = emptyMap())
        }

        val inner = trimmed.removePrefix("do").trim().removeSurrounding("(", ")")
        val fields = mutableMapOf<String, String>()
        val regex = Regex("""(\w+)\s*=\s*(?:\[(.*?)\]|\"(.*?)\"|'([^']*)'|([^,)]+))""")
        regex.findAll(inner).forEach { matchResult ->
            val key = matchResult.groupValues[1]
            val value = matchResult.groupValues.drop(2).firstOrNull { it.isNotEmpty() } ?: ""
            fields[key] = value
        }

        return ParsedAgentAction(metadata = "do", actionName = fields["action"], fields = fields)
    }
}

private suspend fun useFullscreenStatusIndicatorForAgent(context: Context, agentId: String) {
    val floatingService = FloatingChatService.getInstance()
    if (floatingService != null) {
        floatingService.setStatusIndicatorVisible(true)
    } else {
        AppLogger.d("PhoneAgent", "[$agentId] No FloatingChatService instance, using standalone rainbow border overlay")
        UIAutomationProgressOverlay.getInstance(context).setBorderEnabled(true)
    }
}

private suspend fun useVirtualScreenIndicatorForAgent(context: Context, agentId: String) {
    UIAutomationProgressOverlay.getInstance(context).setBorderEnabled(false)
    try {
        val overlay = VirtualDisplayOverlay.getInstance(context, agentId)
        overlay.setDisplayBorderVisible(true)
    } catch (e: Exception) {
        AppLogger.e("PhoneAgent", "[$agentId] Error enabling virtual display border indicator", e)
    }
    val floatingService = FloatingChatService.getInstance()
    floatingService?.setStatusIndicatorVisible(false)
}

private suspend fun clearAgentIndicators(context: Context, agentId: String) {
    UIAutomationProgressOverlay.getInstance(context).setBorderEnabled(false)
    try {
        val overlay = VirtualDisplayOverlay.getInstance(context, agentId)
        overlay.setDisplayBorderVisible(false)
    } catch (e: Exception) {
        AppLogger.e("PhoneAgent", "[$agentId] Error disabling virtual display border indicator", e)
    }
    val floatingService = FloatingChatService.getInstance()
    floatingService?.setStatusIndicatorVisible(false)
}

/** Handles the execution of parsed actions. */
class ActionHandler(
    private val context: Context,
    private var screenWidth: Int,
    private var screenHeight: Int,
    private val toolImplementations: ToolImplementations
) {
    private var agentId: String = "default"
    private var appPackagesSyncedFromTool = false
    private val aiToolManager: AIToolHandler by lazy { AIToolHandler.getInstance(context) }

    // ==================== 帧泵会话状态（live_pipeline_completion 步骤 2） ====================

    /** 特权档主屏泵序号跟踪；-1 表示尚未取过帧 */
    private var lastLiveFrameSequence: Long = -1L

    /** 最近两帧的 imageId：模型据此对照画面变化，更早帧从历史与池中逐出 */
    private val recentFrameImageIds = ArrayDeque<String>(2)

    /** 本步画面是否与上一步相同（泵超时未出新帧） */
    var screenUnchangedSinceLastStep: Boolean = false
        private set

    /** 取一帧：首帧走轮询取当前画面，此后等待比自己手中更新的帧（静默画面超时返回 null）。 */
    private suspend fun awaitLiveFrame(): LiveScreenMirror.Frame? {
        if (lastLiveFrameSequence < 0) {
            val bitmap = LiveScreenMirror.captureFrame()
            lastLiveFrameSequence = LiveScreenMirror.latestSequence()
            return bitmap?.let { LiveScreenMirror.Frame(it, lastLiveFrameSequence, 0L) }
        }
        val frame = LiveScreenMirror.awaitFreshFrame(lastLiveFrameSequence)
        if (frame != null) {
            lastLiveFrameSequence = frame.sequence
        }
        return frame
    }

    /** 从 link 标签提取 imageId（saveCompressedScreenshotFromBitmap 的产物格式）。 */
    private fun linkImageId(link: String): String? =
        Regex("""<link type="image" id="([^"]+)"></link>""").find(link)?.groupValues?.get(1)

    /** 登记新帧 imageId，保留最近两帧：被逐出者从历史 prompt 与图片池同步清除。 */
    private fun rememberFrameImage(imageId: String?, history: MutableList<Pair<String, String>>) {
        if (imageId.isNullOrBlank() || recentFrameImageIds.lastOrNull() == imageId) return
        recentFrameImageIds.addLast(imageId)
        while (recentFrameImageIds.size > 2) {
            val evicted = recentFrameImageIds.removeFirst()
            val pattern = Regex("""\s*<link type="image" id="$evicted"></link>""")
            for (i in history.indices) {
                val (role, content) = history[i]
                if (content.contains(evicted)) {
                    history[i] = role to content.replace(pattern, "").trim()
                }
            }
            ImagePoolManager.removeImage(evicted)
        }
    }

    /** 会话结束/重置：清空帧状态并释放双帧池。 */
    fun resetFrameSession() {
        lastLiveFrameSequence = -1L
        recentFrameImageIds.clear()
        screenUnchangedSinceLastStep = false
    }

    fun setAgentId(id: String) {
        agentId = id
    }

    data class ActionExecResult(
        val success: Boolean,
        val shouldFinish: Boolean,
        val message: String?
    )

    companion object {
        private const val POST_LAUNCH_DELAY_MS = 1000L
        private const val POST_NON_WAIT_ACTION_DELAY_MS = 500L
    }

    private data class DisplayUsageContext(
        val isPrivilegedLevel: Boolean,
        val virtualDisplayId: Int?
    ) {
        val canInjectOnDisplay: Boolean get() = isPrivilegedLevel && virtualDisplayId != null
    }

    private fun isMainScreenAgent(): Boolean = agentId.isBlank() || agentId == "default"

    private fun resolveDisplayUsageContext(): DisplayUsageContext {
        val permissionState = resolvePrivilegedExecutionState(
            context = context,
            androidPermissionPreferences = androidPermissionPreferences,
            onExperimentalFlagReadError = { e ->
                AppLogger.e("ActionHandler", "[$agentId] Error reading experimental virtual display flag", e)
            }
        )
        if (isMainScreenAgent()) {
            // 主屏输入直接走特权档工具链（PrivilegedSystemApi 默认屏注入）
            return DisplayUsageContext(
                isPrivilegedLevel = permissionState.isPrivilegedLevel,
                virtualDisplayId = null
            )
        }
        val displayId = try {
            NativeVirtualDisplay.getDisplayId(agentId)
        } catch (e: Exception) {
            AppLogger.e("ActionHandler", "[$agentId] Error getting native display id", e)
            null
        }
        return DisplayUsageContext(
            isPrivilegedLevel = permissionState.isPrivilegedLevel,
            virtualDisplayId = displayId
        )
    }

    suspend fun captureScreenshotForAgent(history: MutableList<Pair<String, String>>): String? {
        val displayCtx = resolveDisplayUsageContext()
        val floatingService = FloatingChatService.getInstance()
        val progressOverlay = UIAutomationProgressOverlay.getInstance(context)

        var screenshotLink: String? = null
        var dimensions: Pair<Int, Int>? = null
        screenUnchangedSinceLastStep = false

        if (displayCtx.isPrivilegedLevel && isMainScreenAgent()) {
            // 帧泵路径（live_pipeline_completion 步骤 2）：等稳定新帧，画面未变时
            // 复用上帧 imageId 并显式告知模型；指示器已随会话常隐藏，无逐帧遮罩
            val frame = awaitLiveFrame()
            if (frame != null) {
                val (link, dims) = saveCompressedScreenshotFromBitmap(frame.bitmap)
                frame.bitmap.recycle()
                if (link != null) {
                    screenshotLink = link
                    dimensions = dims
                    rememberFrameImage(linkImageId(link), history)
                }
            } else {
                AppLogger.i("ActionHandler", "[$agentId] screen unchanged since sequence $lastLiveFrameSequence")
                val reusedImageId = recentFrameImageIds.lastOrNull()
                if (reusedImageId != null) {
                    screenUnchangedSinceLastStep = true
                    screenshotLink = "<link type=\"image\" id=\"$reusedImageId\"></link>"
                }
            }
        } else {
            try {
                // 非泵路径保留逐帧遮罩：截屏瞬间保持画面干净
                floatingService?.setStatusIndicatorVisible(false)
                progressOverlay.setOverlayVisible(false)
                delay(200)

                if (displayCtx.canInjectOnDisplay && !isMainScreenAgent()) {
                    val (link, dims) = captureScreenshotViaNativeDisplay()
                    screenshotLink = link
                    dimensions = dims
                }

                if (screenshotLink == null) {
                    val screenshotTool = buildScreenshotTool()
                    val (bitmap, fallbackDims) = toolImplementations.captureScreenshotBitmap(screenshotTool)

                    if (bitmap != null) {
                        val (compressedLink, rawDims) = saveCompressedScreenshotFromBitmap(bitmap)
                        screenshotLink = compressedLink
                        dimensions = fallbackDims ?: rawDims
                        bitmap.recycle()
                    }
                }
            } finally {
                val hasDisplayNow = try {
                    NativeVirtualDisplay.getDisplayId(agentId) != null
                } catch (e: Exception) {
                    AppLogger.e("ActionHandler", "[$agentId] Error checking native display state in finally", e)
                    false
                }
                if (isMainScreenAgent() || !hasDisplayNow) {
                    floatingService?.setStatusIndicatorVisible(true)
                }
                progressOverlay.setOverlayVisible(true)
            }
        }

        if (dimensions != null) {
            screenWidth = dimensions.first
            screenHeight = dimensions.second
        }
        return screenshotLink
    }

    private fun buildScreenshotTool(): AITool {
        return AITool(
            name = "capture_screenshot",
            parameters = emptyList()
        )
    }

    private suspend fun captureScreenshotViaNativeDisplay(): Pair<String?, Pair<Int, Int>?> {
        return try {
            val session = NativeVirtualDisplay.getSession(agentId)
                ?: return Pair(null, null)
            val bitmap = session.refreshAndCaptureBitmap()
            if (bitmap == null) {
                AppLogger.w("ActionHandler", "[$agentId] native display returned no frame")
                Pair(null, null)
            } else {
                val result = saveCompressedScreenshotFromBitmap(bitmap)
                bitmap.recycle()
                result
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AppLogger.e("ActionHandler", "[$agentId] native display screenshot failed", e)
            Pair(null, null)
        }
    }

    private fun saveCompressedScreenshotFromBitmap(bitmap: Bitmap): Pair<String?, Pair<Int, Int>?> {
        return try {
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            val imageId = ImagePoolManager.addImageFromBitmap(
                bitmap = bitmap,
                mimeType = if (bitmap.hasAlpha()) "image/png" else "image/jpeg",
                options = buildScreenshotRegistrationOptions()
            )
            if (imageId == "error") {
                Pair(null, null)
            } else {
                Pair("<link type=\"image\" id=\"$imageId\"></link>", Pair(originalWidth, originalHeight))
            }
        } catch (e: Exception) {
            AppLogger.e("ActionHandler", "[$agentId] Error saving compressed screenshot", e)
            Pair(null, null)
        }
    }

    private fun buildScreenshotRegistrationOptions(): ImageRegistrationOptions {
        val prefs = DisplayPreferencesManager.getInstance(context)
        val format = prefs.getScreenshotFormat().uppercase(Locale.getDefault())
        val outputFormat =
            when (format) {
                "JPG", "JPEG" -> ImageOutputFormat.JPEG
                else -> ImageOutputFormat.PNG
            }
        return ImageRegistrationOptions(
            scalePercent = prefs.getScreenshotScalePercent().coerceIn(1, 100),
            outputFormat = outputFormat,
            jpegQuality = prefs.getScreenshotQuality().coerceIn(1, 100),
            normalizeExif = true,
            maxLongEdge = 0
        )
    }

    suspend fun executeAgentAction(parsed: ParsedAgentAction): ActionExecResult {
        val actionName = parsed.actionName ?: return fail(message = "Missing action name")
        val fields = parsed.fields

        val displayCtx = resolveDisplayUsageContext()
        return when (actionName) {
            "Launch" -> {
                val app = fields["app"]?.takeIf { it.isNotBlank() } ?: return fail(message = "No app name specified for Launch")
                val packageName = resolveAppPackageName(app)
                try {
                    if (displayCtx.isPrivilegedLevel && !isMainScreenAgent()) {
                        val metrics = context.resources.displayMetrics
                        val session = try {
                            NativeVirtualDisplay.ensureDisplay(
                                context,
                                agentId,
                                metrics.widthPixels,
                                metrics.heightPixels,
                                metrics.densityDpi
                            )
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            null
                        }
                        val launched = session?.launchApp(context, packageName) ?: false

                        if (launched) {
                            try {
                                VirtualDisplayOverlay.getInstance(context, agentId).updateCurrentAppPackageName(packageName)
                            } catch (_: Exception) {}
                            useVirtualScreenIndicatorForAgent(context, agentId)
                            delay(POST_LAUNCH_DELAY_MS)
                            ok()
                        } else {
                            fail(message = "Failed to launch $packageName on virtual display")
                        }
                    } else {
                        val result = aiToolManager.executeTool(
                            AITool("start_app", listOf(ToolParameter("package_name", packageName)))
                        )
                        if (result.success) {
                            delay(POST_LAUNCH_DELAY_MS)
                            ok()
                        } else {
                            fail(message = result.error ?: "Failed to launch app: $packageName")
                        }
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    fail(message = "Exception while launching app: ${e.message}")
                }
            }
            "Tap" -> {
                val element = fields["element"] ?: return fail(message = "No element for Tap")
                val (x, y) = parseRelativePoint(element) ?: return fail(message = "Invalid coordinates for Tap: $element")
                val exec = withAgentUiHiddenForAction(displayCtx) {
                    if (displayCtx.canInjectOnDisplay) {
                        val session = NativeVirtualDisplay.getSession(agentId)
                            ?: return@withAgentUiHiddenForAction fail(message = "Virtual display session gone")
                        val okTap = session.tap(x, y)
                        if (okTap) ok() else fail(message = "Virtual display TAP failed at ($x,$y)")
                    } else {
                        val params = listOf(ToolParameter("x", x.toString()), ToolParameter("y", y.toString()))
                        val result = toolImplementations.tap(AITool("tap", params))
                        if (result.success) ok() else fail(message = result.error ?: "Tap failed")
                    }
                }
                if (exec.success && !exec.shouldFinish) delay(POST_NON_WAIT_ACTION_DELAY_MS)
                exec
            }
            "Type" -> {
                val text = fields["text"] ?: ""
                val exec = withAgentUiHiddenForAction(displayCtx) {
                    if (displayCtx.canInjectOnDisplay) {
                        try {
                            val session = NativeVirtualDisplay.getSession(agentId)
                                ?: return@withAgentUiHiddenForAction fail(message = "Virtual display session gone")
                            // 全选清空后按 ACTION_MULTIPLE 字符事件注入：无剪贴板依赖
                            //（后台剪贴板写对非焦点应用关闭），ASCII/CJK 统一走字符通道
                            val selectedAll = session.keyWithMeta(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
                            if (!selectedAll) {
                                return@withAgentUiHiddenForAction fail(message = "Virtual display select-all failed")
                            }
                            delay(80)
                            session.key(KeyEvent.KEYCODE_DEL)
                            delay(120)
                            if (text.isEmpty()) return@withAgentUiHiddenForAction ok()
                            val injected = PrivilegedSystemApi.injectCharactersOnDisplay(text, session.displayId)
                            if (injected) ok() else fail(message = "Virtual display text injection failed")
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            fail(message = "Error typing on virtual display: ${e.message}")
                        }
                    } else {
                        val params = listOf(ToolParameter("text", text))
                        val result = toolImplementations.setInputText(AITool("set_input_text", params))
                        if (result.success) ok() else fail(message = result.error ?: "Type failed")
                    }
                }
                if (exec.success && !exec.shouldFinish) delay(POST_NON_WAIT_ACTION_DELAY_MS)
                exec
            }
            "Swipe" -> {
                val start = fields["start"] ?: return fail(message = "Missing swipe start")
                val end = fields["end"] ?: return fail(message = "Missing swipe end")
                val (sx, sy) = parseRelativePoint(start) ?: return fail(message = "Invalid swipe start")
                val (ex, ey) = parseRelativePoint(end) ?: return fail(message = "Invalid swipe end")
                val exec = withAgentUiHiddenForAction(displayCtx) {
                    if (displayCtx.canInjectOnDisplay) {
                        val session = NativeVirtualDisplay.getSession(agentId)
                            ?: return@withAgentUiHiddenForAction fail(message = "Virtual display session gone")
                        val okSwipe = session.swipe(sx, sy, ex, ey, 300L)
                        if (okSwipe) ok() else fail(message = "Virtual display SWIPE failed")
                    } else {
                        val params = listOf(
                            ToolParameter("start_x", sx.toString()), ToolParameter("start_y", sy.toString()),
                            ToolParameter("end_x", ex.toString()), ToolParameter("end_y", ey.toString())
                        )
                        val result = toolImplementations.swipe(AITool("swipe", params))
                        if (result.success) ok() else fail(message = result.error ?: "Swipe failed")
                    }
                }
                if (exec.success && !exec.shouldFinish) delay(POST_NON_WAIT_ACTION_DELAY_MS)
                exec
            }
            "Back" -> {
                val exec = withAgentUiHiddenForAction(displayCtx) {
                    if (displayCtx.canInjectOnDisplay) {
                        val session = NativeVirtualDisplay.getSession(agentId)
                            ?: return@withAgentUiHiddenForAction fail(message = "Virtual display session gone")
                        val okKey = session.key(KeyEvent.KEYCODE_BACK)
                        if (okKey) ok() else fail(message = "Virtual display BACK failed")
                    } else {
                        val params = listOf(ToolParameter("key_code", "KEYCODE_BACK"))
                        val result = toolImplementations.pressKey(AITool("press_key", params))
                        if (result.success) ok() else fail(message = result.error ?: "Back failed")
                    }
                }
                if (exec.success && !exec.shouldFinish) delay(POST_NON_WAIT_ACTION_DELAY_MS)
                exec
            }
            "Home" -> {
                val exec = withAgentUiHiddenForAction(displayCtx) {
                    if (displayCtx.canInjectOnDisplay) {
                        val session = NativeVirtualDisplay.getSession(agentId)
                            ?: return@withAgentUiHiddenForAction fail(message = "Virtual display session gone")
                        val okKey = session.key(KeyEvent.KEYCODE_HOME)
                        if (okKey) ok() else fail(message = "Virtual display HOME failed")
                    } else {
                        val params = listOf(ToolParameter("key_code", "KEYCODE_HOME"))
                        val result = toolImplementations.pressKey(AITool("press_key", params))
                        if (result.success) ok() else fail(message = result.error ?: "Home failed")
                    }
                }
                if (exec.success && !exec.shouldFinish) delay(POST_NON_WAIT_ACTION_DELAY_MS)
                exec
            }
            "Wait" -> {
                val seconds = fields["duration"]?.replace("seconds", "")?.trim()?.toDoubleOrNull() ?: 1.0
                delay((seconds * 1000).toLong().coerceAtLeast(0L))
                ok()
            }
            "Take_over" -> ok(shouldFinish = true, message = fields["message"] ?: "User takeover required")
            else -> fail(message = "Unknown action: $actionName")
        }
    }

    private suspend fun withAgentUiHiddenForAction(
        displayCtx: DisplayUsageContext,
        block: suspend () -> ActionExecResult
    ): ActionExecResult {
        val shouldHideUiDuringAction = isMainScreenAgent() || !displayCtx.canInjectOnDisplay
        if (!shouldHideUiDuringAction) return block()

        val floatingService = FloatingChatService.getInstance()
        val progressOverlay = UIAutomationProgressOverlay.getInstance(context)
        try {
            if (isMainScreenAgent()) {
                floatingService?.setStatusIndicatorVisible(false)
            }
            progressOverlay.setOverlayVisible(false)
            delay(200)
            return block()
        } finally {
            if (isMainScreenAgent()) {
                floatingService?.setStatusIndicatorVisible(true)
            }
            progressOverlay.setOverlayVisible(true)
        }
    }

    private fun ok(shouldFinish: Boolean = false, message: String? = null) = ActionExecResult(true, shouldFinish, message)
    private fun fail(shouldFinish: Boolean = false, message: String) = ActionExecResult(false, shouldFinish, message)

    private fun parseRelativePoint(value: String): Pair<Int, Int>? {
        val parts = value.trim().removeSurrounding("[", "]").split(",").map { it.trim() }
        if (parts.size < 2) return null
        val relX = parts[0].toIntOrNull() ?: return null
        val relY = parts[1].toIntOrNull() ?: return null
        return (relX / 1000.0 * screenWidth).toInt() to (relY / 1000.0 * screenHeight).toInt()
    }

    private suspend fun resolveAppPackageName(app: String): String {
        val trimmed = app.trim()
        val lowered = trimmed.lowercase(Locale.getDefault())
        fun lookup(): String? =
            StandardUITools.APP_PACKAGES[app] ?: StandardUITools.APP_PACKAGES[trimmed] ?: StandardUITools.APP_PACKAGES[lowered]

        lookup()?.let { return it }

        syncAppPackagesFromToolIfNeeded()
        return lookup() ?: trimmed
    }

    private suspend fun syncAppPackagesFromToolIfNeeded() {
        if (appPackagesSyncedFromTool) return
        appPackagesSyncedFromTool = true

        val listResult = aiToolManager.executeTool(AITool("list_installed_apps"))
        if (!listResult.success) {
            AppLogger.w("PhoneAgent", "[$agentId] Failed to sync app packages from tool layer: ${listResult.error}")
            return
        }

        val appListData = listResult.result as? AppListData ?: return
        val discoveredPackages = mutableMapOf<String, String>()

        appListData.packages.forEach { entry ->
            val parsed = parseToolAppEntry(entry) ?: return@forEach
            val (appName, packageName) = parsed
            if (appName.isBlank() || packageName.isBlank()) return@forEach

            discoveredPackages.putIfAbsent(appName, packageName)
            discoveredPackages.putIfAbsent(appName.lowercase(Locale.getDefault()), packageName)
        }

        if (discoveredPackages.isNotEmpty()) {
            StandardUITools.addAppPackages(discoveredPackages)
        }
    }

    private fun parseToolAppEntry(entry: String): Pair<String, String>? {
        val left = entry.lastIndexOf('(')
        val right = entry.lastIndexOf(')')
        if (left < 0 || right <= left) return null

        val appName = entry.substring(0, left).trim()
        val packageName = entry.substring(left + 1, right).trim()
        if (packageName.isBlank()) return null
        return Pair(if (appName.isBlank()) packageName else appName, packageName)
    }
}

/** Interface for providing tool implementations to the ActionHandler. */
interface ToolImplementations {
    suspend fun tap(tool: AITool): ToolResult
    suspend fun longPress(tool: AITool): ToolResult
    suspend fun setInputText(tool: AITool): ToolResult
    suspend fun swipe(tool: AITool): ToolResult
    suspend fun pressKey(tool: AITool): ToolResult
    suspend fun captureScreenshot(tool: AITool): Pair<String?, Pair<Int, Int>?>
    suspend fun captureScreenshotBitmap(tool: AITool): Pair<Bitmap?, Pair<Int, Int>?> {
        val (filePath, dimensions) = captureScreenshot(tool)
        if (filePath == null) {
            return Pair(null, dimensions)
        }

        val bitmap = BitmapFactory.decodeFile(filePath) ?: return Pair(null, dimensions)
        val resolvedDimensions = dimensions ?: Pair(bitmap.width, bitmap.height)
        return Pair(bitmap, resolvedDimensions)
    }
}
