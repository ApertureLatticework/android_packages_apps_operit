package com.ai.assistance.operit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ai.assistance.operit.data.dao.DocumentChunkDao
import com.ai.assistance.operit.data.dao.MemoryAutoSaveCandidateDao
import com.ai.assistance.operit.data.dao.MemoryDao
import com.ai.assistance.operit.data.dao.MemoryLinkDao
import com.ai.assistance.operit.data.model.DocumentChunk
import com.ai.assistance.operit.data.model.Memory
import com.ai.assistance.operit.data.model.MemoryAutoSaveCandidate
import com.ai.assistance.operit.data.model.MemoryDbConverters
import com.ai.assistance.operit.data.model.MemoryLink
import com.ai.assistance.operit.data.model.MemoryTag
import com.ai.assistance.operit.data.model.MemoryTagJunction

/**
 * 记忆库数据库（原 ObjectBox 双轨的记忆/向量块存储线，统一并入 Room）。
 * 按 profileId 一库一实例，由 [MemoryDatabaseManager] 管理生命周期。
 */
@Database(
    entities = [
        Memory::class,
        MemoryTag::class,
        MemoryTagJunction::class,
        MemoryLink::class,
        DocumentChunk::class,
        MemoryAutoSaveCandidate::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(MemoryDbConverters::class)
abstract class MemoryDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun memoryLinkDao(): MemoryLinkDao
    abstract fun documentChunkDao(): DocumentChunkDao
    abstract fun memoryAutoSaveCandidateDao(): MemoryAutoSaveCandidateDao
}
