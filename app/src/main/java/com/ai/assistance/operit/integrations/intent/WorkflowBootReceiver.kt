package com.ai.assistance.operit.integrations.intent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ai.assistance.operit.data.repository.WorkflowRepository
import com.ai.assistance.operit.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver for boot completed event
 *
 * Re-schedules all enabled workflows after device reboot.
 * 自 Tasker 集成线裁撤后从 integrations/tasker 迁居至此，逻辑不变。
 */
class WorkflowBootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "WorkflowBootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        AppLogger.d(TAG, "Device booted, rescheduling workflows")

        // Use goAsync to allow async work
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = WorkflowRepository(context.applicationContext)
                val result = repository.getAllWorkflows()

                result.getOrNull()?.forEach { workflow ->
                    if (workflow.enabled) {
                        repository.scheduleWorkflow(workflow.id)
                        AppLogger.d(TAG, "Rescheduled workflow: ${workflow.name}")
                    }
                }
            } catch (e: Exception) {
                AppLogger.e(TAG, "Error rescheduling workflows after boot", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
