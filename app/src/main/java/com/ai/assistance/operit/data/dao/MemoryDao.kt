package com.ai.assistance.operit.data.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ai.assistance.operit.data.model.Memory
import com.ai.assistance.operit.data.model.MemoryTag
import com.ai.assistance.operit.data.model.MemoryTagJunction

/** 记忆-标签对（junction 联查结果，用于批量水合 Memory.tags） */
data class MemoryTagPair(
    val memoryId: Long,
    @Embedded val tag: MemoryTag
)

@Dao
interface MemoryDao {

    // --- Memory CRUD ---

    @Query("SELECT * FROM memory")
    suspend fun getAll(): List<Memory>

    @Query("SELECT * FROM memory WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Memory?

    @Query("SELECT * FROM memory WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<Memory>

    @Query("SELECT * FROM memory WHERE uuid = :uuid LIMIT 1")
    suspend fun getByUuid(uuid: String): Memory?

    @Query("SELECT * FROM memory WHERE uuid IN (:uuids)")
    suspend fun getByUuids(uuids: List<String>): List<Memory>

    @Query("SELECT * FROM memory WHERE title = :title LIMIT 1")
    suspend fun getFirstByTitle(title: String): Memory?

    @Query("SELECT * FROM memory WHERE title = :title")
    suspend fun getByTitle(title: String): List<Memory>

    @Query("SELECT * FROM memory WHERE isDocumentNode = 0")
    suspend fun getNonDocumentNodes(): List<Memory>

    /** SQLite LIKE 对 ASCII 默认大小写不敏感，对 CJK 无影响，等价原 CASE_INSENSITIVE contains */
    @Query("SELECT * FROM memory WHERE title LIKE '%' || :fragment || '%'")
    suspend fun titleContains(fragment: String): List<Memory>

    @Insert
    suspend fun insert(memory: Memory): Long

    @Update
    suspend fun update(memory: Memory)

    /** id 为 0 走插入并原地回写新 id，否则按主键更新（避免 REPLACE 连带 CASCADE 清空关联表） */
    @Transaction
    suspend fun upsert(memory: Memory): Long {
        return if (memory.id == 0L) {
            memory.id = insert(memory)
            memory.id
        } else {
            update(memory)
            memory.id
        }
    }

    @Transaction
    suspend fun upsertAll(memories: List<Memory>) {
        memories.forEach { upsert(it) }
    }

    @Query("DELETE FROM memory WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM memory WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    // --- Tag CRUD ---

    @Query("SELECT * FROM memory_tag WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): MemoryTag?

    @Insert
    suspend fun insertTag(tag: MemoryTag): Long

    @Transaction
    suspend fun getOrCreateTag(name: String): MemoryTag {
        getTagByName(name)?.let { return it }
        val id = insertTag(MemoryTag(name = name))
        return MemoryTag(id = id, name = name)
    }

    // --- Junction（memory_tag_junction）---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkTag(junction: MemoryTagJunction)

    @Query("DELETE FROM memory_tag_junction WHERE memoryId = :memoryId")
    suspend fun clearTagsForMemory(memoryId: Long)

    /** 以记忆为单位整表替换标签关联 */
    @Transaction
    suspend fun replaceTagsForMemory(memoryId: Long, tagIds: List<Long>) {
        clearTagsForMemory(memoryId)
        tagIds.forEach { linkTag(MemoryTagJunction(memoryId = memoryId, tagId = it)) }
    }

    @Query(
        """
        SELECT j.memoryId AS memoryId, t.id AS id, t.name AS name
        FROM memory_tag_junction j
        INNER JOIN memory_tag t ON t.id = j.tagId
        WHERE j.memoryId IN (:memoryIds)
        """
    )
    suspend fun getTagPairs(memoryIds: List<Long>): List<MemoryTagPair>

    @Query(
        """
        SELECT j.memoryId AS memoryId, t.id AS id, t.name AS name
        FROM memory_tag_junction j
        INNER JOIN memory_tag t ON t.id = j.tagId
        """
    )
    suspend fun getAllTagPairs(): List<MemoryTagPair>
}
