package com.ai.assistance.operit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ai.assistance.operit.data.model.MemoryLink

@Dao
interface MemoryLinkDao {

    @Query("SELECT * FROM memory_link")
    suspend fun getAll(): List<MemoryLink>

    @Query("SELECT * FROM memory_link WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MemoryLink?

    @Query("SELECT * FROM memory_link WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<MemoryLink>

    @Query("SELECT * FROM memory_link WHERE sourceId = :memoryId")
    suspend fun getOutgoing(memoryId: Long): List<MemoryLink>

    @Query("SELECT * FROM memory_link WHERE targetId = :memoryId")
    suspend fun getIncoming(memoryId: Long): List<MemoryLink>

    @Query("SELECT * FROM memory_link WHERE sourceId IN (:memoryIds)")
    suspend fun getOutgoingFor(memoryIds: List<Long>): List<MemoryLink>

    @Query("SELECT * FROM memory_link WHERE targetId IN (:memoryIds)")
    suspend fun getIncomingFor(memoryIds: List<Long>): List<MemoryLink>

    @Insert
    suspend fun insert(link: MemoryLink): Long

    @Insert
    suspend fun insertAll(links: List<MemoryLink>): List<Long>

    @Update
    suspend fun update(link: MemoryLink)

    @Update
    suspend fun updateAll(links: List<MemoryLink>)

    @Query("DELETE FROM memory_link WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: Collection<Long>)

    @Query("DELETE FROM memory_link WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
