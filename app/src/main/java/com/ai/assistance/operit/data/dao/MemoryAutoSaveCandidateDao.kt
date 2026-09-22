package com.ai.assistance.operit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ai.assistance.operit.data.model.MemoryAutoSaveCandidate

@Dao
interface MemoryAutoSaveCandidateDao {

    @Query(
        "SELECT * FROM memory_auto_save_candidate WHERE status IN (:statuses) ORDER BY createdAt ASC"
    )
    suspend fun getByStatuses(statuses: List<String>): List<MemoryAutoSaveCandidate>

    @Query("SELECT * FROM memory_auto_save_candidate WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MemoryAutoSaveCandidate?

    @Query("SELECT * FROM memory_auto_save_candidate WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<MemoryAutoSaveCandidate>

    @Insert
    suspend fun insert(candidate: MemoryAutoSaveCandidate): Long

    @Update
    suspend fun updateAll(candidates: List<MemoryAutoSaveCandidate>)

    @Query("DELETE FROM memory_auto_save_candidate WHERE id = :id")
    suspend fun deleteById(id: Long)
}
