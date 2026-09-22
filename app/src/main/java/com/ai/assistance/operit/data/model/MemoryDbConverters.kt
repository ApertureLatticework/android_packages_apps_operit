package com.ai.assistance.operit.data.model

import androidx.room.TypeConverter
import java.nio.ByteBuffer
import java.util.Date

/**
 * Memory 库专用 Room 类型转换器：
 * - Embedding <-> ByteArray（float 数组按小端 ByteBuffer 编码，沿用原向量存储格式）
 * - Date <-> Long（时间戳毫秒）
 *
 * 注册于 [com.ai.assistance.operit.data.db.MemoryDatabase]，不影响 AppDatabase。
 */
class MemoryDbConverters {
    @TypeConverter
    fun embeddingToDb(value: Embedding?): ByteArray? {
        if (value == null) return null
        val vector = value.vector
        if (vector.isEmpty()) return null
        val buffer = ByteBuffer.allocate(vector.size * 4)
        buffer.asFloatBuffer().put(vector)
        return buffer.array()
    }

    @TypeConverter
    fun embeddingFromDb(value: ByteArray?): Embedding? {
        if (value == null || value.isEmpty()) return null
        val floatBuffer = ByteBuffer.wrap(value).asFloatBuffer()
        val floatArray = FloatArray(floatBuffer.remaining())
        floatBuffer.get(floatArray)
        return Embedding(floatArray)
    }

    @TypeConverter
    fun dateToDb(value: Date?): Long? = value?.time

    @TypeConverter
    fun dateFromDb(value: Long?): Date? = value?.let(::Date)
}
