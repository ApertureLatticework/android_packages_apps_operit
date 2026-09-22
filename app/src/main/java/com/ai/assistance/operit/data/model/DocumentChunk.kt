package com.ai.assistance.operit.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 表示外部文档中的一个内容区块（例如，一个段落）。
 * 每个区块都有自己的内容和向量嵌入，以便进行独立的语义搜索。
 */
@Entity(
    tableName = "document_chunk",
    indices = [Index("memoryId")],
    foreignKeys = [
        ForeignKey(
            entity = Memory::class,
            parentColumns = ["id"],
            childColumns = ["memoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DocumentChunk(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,

    // 所属文档记忆的 ID（替代 ObjectBox ToOne 关系）
    var memoryId: Long = 0,

    // 区块的文本内容
    var content: String = "",

    // 区块在文档中的顺序索引
    var chunkIndex: Int = 0,

    // 文本内容的向量嵌入（MemoryDbConverters 编码为 BLOB）
    var embedding: Embedding? = null
)
