package com.ai.assistance.operit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ai.assistance.operit.data.model.DocumentChunk

@Dao
interface DocumentChunkDao {

    @Query("SELECT * FROM document_chunk")
    suspend fun getAll(): List<DocumentChunk>

    @Query("SELECT * FROM document_chunk WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DocumentChunk?

    @Query("SELECT * FROM document_chunk WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<DocumentChunk>

    @Query("SELECT * FROM document_chunk WHERE memoryId = :memoryId ORDER BY chunkIndex ASC")
    suspend fun getByMemoryId(memoryId: Long): List<DocumentChunk>

    @Insert
    suspend fun insert(chunk: DocumentChunk): Long

    @Insert
    suspend fun insertAll(chunks: List<DocumentChunk>): List<Long>

    @Update
    suspend fun update(chunk: DocumentChunk)

    @Update
    suspend fun updateAll(chunks: List<DocumentChunk>)

    @Query("DELETE FROM document_chunk WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: Collection<Long>)
}
