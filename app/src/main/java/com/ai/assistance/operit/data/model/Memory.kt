package com.ai.assistance.operit.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * 核心记忆单元 (Memory Unit)
 * 代表一个独立的知识片段、事件、概念或任何AI需要记住的东西。
 *
 * Room 实体。原 ObjectBox ToMany/ToOne/Backlink 关系改为显式外键列
 * （MemoryLink.sourceId/targetId、DocumentChunk.memoryId、MemoryTagJunction 关联表）；
 * 下列 @Ignore 集合字段仅作为仓储层读取时填充的只读快照，不参与持久化，
 * 写操作必须走 MemoryRepository 提供的专用方法。
 */
@Entity(
    tableName = "memory",
    indices = [Index("uuid"), Index("folderPath")]
)
data class Memory(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var uuid: String = UUID.randomUUID().toString(),

    // --- 核心内容 (Core Content) ---
    var title: String = "", // 记忆的简短标题/摘要
    var content: String = "", // 详细内容 (可以是文本、JSON、文件路径等)
    var contentType: String = "text/plain", // 内容类型 (e.g., "text/plain", "image/jpeg", "application/json")

    // --- 元数据 (Metadata) ---
    var source: String = "unknown", // 来源 (e.g., "user_input", "chat_summary", "web_scrape")
    var credibility: Float = 0.5f, // 可信度 (0.0 to 1.0)
    var importance: Float = 0.5f,  // 重要性 (0.0 to 1.0)

    // 如果这是一个文档节点，则存储文档的路径/URI
    var documentPath: String? = null,
    // 标记此记忆是否代表一个外部文档
    var isDocumentNode: Boolean = false,
    // 如果这是一个文档节点，则存储其区块索引文件的路径
    var chunkIndexFilePath: String? = null,

    // 文件夹路径，用于分类组织记忆（如 "工作/项目A" 或 "生活/健康"）
    // null 视为"未分类"
    var folderPath: String? = null,

    // 文本内容的向量嵌入（MemoryDbConverters 编码为 BLOB）
    var embedding: Embedding? = null,

    // --- 时间戳 (Timestamps) ---
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var lastAccessedAt: Date = Date()
) {
    /** 该记忆的标签快照（读取时由仓储层填充） */
    @Ignore
    var tags: List<MemoryTag> = emptyList()

    /** 从该记忆出发的关联快照（读取时由仓储层填充，两端对象已水合） */
    @Ignore
    var links: List<MemoryLink> = emptyList()

    /** 指向该记忆的入边快照（读取时由仓储层填充，两端对象已水合） */
    @Ignore
    var backlinks: List<MemoryLink> = emptyList()

    /** 文档节点的区块快照（仅 getChunksForMemory 等专用读取路径填充） */
    @Ignore
    var documentChunks: List<DocumentChunk> = emptyList()
}

/**
 * 记忆标签 (Memory Tag)
 * 用于对记忆进行分类和组织。
 * 原 ObjectBox parent 自引用与 memories 反向关系在代码库中无任何消费方，随迁移裁除。
 */
@Entity(
    tableName = "memory_tag",
    indices = [Index("name")]
)
data class MemoryTag(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "" // 标签名称
)

/**
 * 记忆-标签多对多关联表（替代 ObjectBox ToMany 隐式关联）。
 */
@Entity(
    tableName = "memory_tag_junction",
    primaryKeys = ["memoryId", "tagId"],
    indices = [Index("tagId")],
    foreignKeys = [
        ForeignKey(
            entity = Memory::class,
            parentColumns = ["id"],
            childColumns = ["memoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MemoryTag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemoryTagJunction(
    val memoryId: Long,
    val tagId: Long
)

/**
 * 记忆关联 (Memory Link)
 * 定义记忆之间的关系。sourceId/targetId 为显式外键（替代 ObjectBox ToOne）。
 */
@Entity(
    tableName = "memory_link",
    indices = [Index("sourceId"), Index("targetId")],
    foreignKeys = [
        ForeignKey(
            entity = Memory::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Memory::class,
            parentColumns = ["id"],
            childColumns = ["targetId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemoryLink(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var type: String = "related", // 关联类型 (e.g., "causes", "explains", "part_of")
    var weight: Float = 1.0f, // 关联强度 (0.0 to 1.0)
    var description: String = "", // 关联的详细描述
    var sourceId: Long = 0,
    var targetId: Long = 0
) {
    /** 源记忆对象快照（读取时由仓储层水合，可为 null 表示悬空引用） */
    @Ignore
    var sourceMemory: Memory? = null

    /** 目标记忆对象快照（读取时由仓储层水合，可为 null 表示悬空引用） */
    @Ignore
    var targetMemory: Memory? = null
}
