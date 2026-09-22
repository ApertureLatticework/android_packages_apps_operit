package com.ai.assistance.operit.data.db

import android.content.Context
import androidx.room.Room
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * 记忆库的多 profile 管理器（替代原 ObjectBoxManager 的职责面）：
 * 按 profileId 维护 [MemoryDatabase] 实例，库文件落位 databases/ 目录。
 */
object MemoryDatabaseManager {
    private const val DEFAULT_DB_NAME = "memory_default.db"

    private val databases = ConcurrentHashMap<String, MemoryDatabase>()
    private val lock = Any()

    fun get(context: Context, profileId: String): MemoryDatabase {
        synchronized(lock) {
            databases[profileId]?.let { return it }
            val db = Room.databaseBuilder(
                context.applicationContext,
                MemoryDatabase::class.java,
                dbNameFor(profileId)
            ).build()
            databases[profileId] = db
            return db
        }
    }

    fun close(profileId: String) {
        synchronized(lock) {
            databases.remove(profileId)?.close()
        }
    }

    /** 物理删除指定 profileId 的数据库（关闭实例并清掉 db/-wal/-shm 文件）。 */
    fun delete(context: Context, profileId: String) {
        synchronized(lock) {
            close(profileId)
            val name = dbNameFor(profileId)
            val dbFile = context.getDatabasePath(name)
            if (dbFile.exists()) dbFile.delete()
            val wal = File(dbFile.parentFile, "$name-wal")
            if (wal.exists()) wal.delete()
            val shm = File(dbFile.parentFile, "$name-shm")
            if (shm.exists()) shm.delete()
        }
    }

    fun closeAll() {
        synchronized(lock) {
            databases.values.forEach { it.close() }
            databases.clear()
        }
    }

    private fun dbNameFor(profileId: String): String {
        return if (profileId == "default") DEFAULT_DB_NAME else "memory_$profileId.db"
    }
}
