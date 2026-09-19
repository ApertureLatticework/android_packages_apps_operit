package com.ai.assistance.operit.data.repository

import android.content.Context
import com.ai.assistance.operit.data.db.MemoryDatabaseManager
import com.ai.assistance.operit.data.model.MemoryAutoSaveCandidate
import java.util.Date

class MemoryAutoSaveCandidateRepository(
    context: Context,
    profileId: String
) {
    private val candidateDao =
        MemoryDatabaseManager.get(context, profileId).memoryAutoSaveCandidateDao()

    suspend fun enqueue(
        chatId: String,
        triggerMessageTimestamp: Long,
        sourceType: String = MemoryAutoSaveCandidate.SOURCE_TYPE_REPLY_FINALIZED_AUTO
    ): Long {
        val now = Date()
        val candidate =
            MemoryAutoSaveCandidate(
                chatId = chatId,
                triggerMessageTimestamp = triggerMessageTimestamp,
                createdAt = now,
                updatedAt = now,
                status = MemoryAutoSaveCandidate.STATUS_PENDING,
                sourceType = sourceType
            )
        return candidateDao.insert(candidate)
    }

    suspend fun enqueueSelectedUserMessages(
        chatId: String,
        triggerMessageTimestamps: List<Long>
    ) {
        val normalizedTimestamps =
            triggerMessageTimestamps
                .filter { it > 0L }
                .distinct()
                .sorted()
        if (chatId.isBlank() || normalizedTimestamps.isEmpty()) return
        normalizedTimestamps.forEach { timestamp ->
            enqueue(
                chatId = chatId,
                triggerMessageTimestamp = timestamp,
                sourceType = MemoryAutoSaveCandidate.SOURCE_TYPE_SELECTED_USER_MESSAGE
            )
        }
    }

    suspend fun getPendingAndFailedCandidates(): List<MemoryAutoSaveCandidate> {
        return candidateDao.getByStatuses(
            listOf(
                MemoryAutoSaveCandidate.STATUS_PENDING,
                MemoryAutoSaveCandidate.STATUS_FAILED
            )
        )
    }

    suspend fun countPendingAndFailedChats(): Int {
        return getPendingAndFailedCandidates()
            .map { it.chatId }
            .filter { it.isNotBlank() }
            .distinct()
            .size
    }

    suspend fun countPendingAndFailedCandidates(): Int {
        return getPendingAndFailedCandidates().size
    }

    suspend fun markProcessing(candidateIds: List<Long>) {
        if (candidateIds.isEmpty()) return
        val candidates = candidateDao.getByIds(candidateIds)
        val now = Date()
        candidates.forEach { candidate ->
            candidate.status = MemoryAutoSaveCandidate.STATUS_PROCESSING
            candidate.updatedAt = now
            candidate.lastError = ""
        }
        candidateDao.updateAll(candidates)
    }

    suspend fun markPending(candidateIds: List<Long>) {
        if (candidateIds.isEmpty()) return
        val candidates = candidateDao.getByIds(candidateIds)
        val now = Date()
        candidates.forEach { candidate ->
            candidate.status = MemoryAutoSaveCandidate.STATUS_PENDING
            candidate.updatedAt = now
            candidate.lastError = ""
        }
        candidateDao.updateAll(candidates)
    }

    suspend fun deleteCandidates(candidateIds: List<Long>) {
        if (candidateIds.isEmpty()) return
        candidateIds.forEach { candidateDao.deleteById(it) }
    }

    suspend fun markFailed(candidateIds: List<Long>, errorMessage: String) {
        if (candidateIds.isEmpty()) return
        val candidates = candidateDao.getByIds(candidateIds)
        val now = Date()
        val normalizedError = errorMessage.take(500)
        candidates.forEach { candidate ->
            candidate.status = MemoryAutoSaveCandidate.STATUS_FAILED
            candidate.attemptCount += 1
            candidate.lastError = normalizedError
            candidate.updatedAt = now
        }
        candidateDao.updateAll(candidates)
    }
}
