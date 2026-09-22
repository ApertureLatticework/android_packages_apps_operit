# 2. ObjectBox 迁移 Room 并删除整线

## 旧实现情况

ObjectBox 承载记忆与向量块存储，面积很小：

- 实体 3 个：`Memory`、`DocumentChunk`、`MemoryAutoSaveCandidate`（均带 `@Index`，前两者带 `@Convert` 将 embedding 转 ByteArray）
- API 引用共 7 处、分布于 7 个文件，集中在记忆与向量检索路径
- 模型文件 `app/objectbox-models/default.json` 已在 git 管理
- 与 Room 双轨并存，各自维护健康检查与恢复逻辑

## 意图

ObjectBox 迁入 Room 后删除整条依赖线，消除双轨。选它而非反向迁移 Room 是因为面积差一个数量级。

## 期待的新实现

- 三个实体改注 Room 注解：ByteArray 映射 BLOB 列，`@Convert(EmbeddingConverter)` 改写为 `TypeConverter`，`@Index` 对应 `@Entity(indices=...)`
- 7 处 ObjectBox API 调用点改为 DAO 调用，行为对齐原查询语义
- 新表与旧 ObjectBox 库文件的一次性数据迁移：仅当确有从通用版换装 ROM 版的用户群时启用，首启检测旧库文件存在则导入后改名为 `.migrated`；ROM-only 定稿则跳过此项直接删旧库识别代码
- 删除项：
    - `io.objectbox` 依赖、ObjectBox Gradle 插件、kapt processor
    - `app/objectbox-models/` 目录
    - `io.objectbox` 相关恢复与健康检查代码

## 作用域

- `data/model/` 三个实体与新增 DAO
- 7 处调用点所在文件
- `app/build.gradle.kts`、`app/objectbox-models/`
- 数据层恢复与健康检查入口

## 验证

- Gradle 单测覆盖：实体读写、embedding 转换、索引查询
- 手动升级路径：旧版数据目录启动新版，记忆与向量数据完整可检索

## 执行结果（2026-09-19 落地）

ROM-only 定稿，旧 ObjectBox 数据迁移整项跳过，无旧库识别代码入库。
与计划的差异：

- 实体实际为六个：Memory、MemoryTag、MemoryTagJunction（新关系表）、MemoryLink、DocumentChunk、MemoryAutoSaveCandidate；原计划只列了三个，Memory.kt 内还压着标签与关联实体
- MemoryProperty 实体与 MemoryTag 的 parent 自引用在代码库零消费方，随迁移裁除，未迁移
- ToMany/ToOne/Backlink 改为显式外键列加 CASCADE，Memory/MemoryLink 上的关系字段降为 @Ignore 只读快照，由仓储层批量水合（hydrateTags/hydrateLinks/hydrateLinkEnds），UI 与工具层读法不变
- MemoryAutoSaveCandidateRepository 全部方法转 suspend（全部调用点已往协程内），ObjectBox 同步 API 契约就此废弃
- upsert 语义对齐原 put：插入时原地回写自增 id，mergeMemories/createMemoryFromDocument 依赖此行为
- 库文件落位 databases/memory_<profileId>.db，原 filesDir/objectbox 目录不再使用；RawSnapshot 快照走 databases 目录自动覆盖，objectbox lock.mdb 特判删除
- io.objectbox 依赖、Gradle 插件、kapt processor、objectbox-models/、root buildscript 块、开源许可名单条目全部移除
