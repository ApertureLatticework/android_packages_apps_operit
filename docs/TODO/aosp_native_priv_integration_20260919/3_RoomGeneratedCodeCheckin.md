# 3. Room 生成代码 check-in 机制

## 旧实现情况

Room 的 `*_Dao_Impl`、`*_Database_Impl` 等实现类由 kapt 在构建期生成于 `app/build/generated/`，不入 git。Soong 只有 javac 注解处理器通道（java_plugin），无 kapt 与 KSP，生成类缺失时源码直接编译失败。这是源码进树的分水岭问题。

## 意图

生成类转为 check-in 源文件，Soong 侧以普通 Java 源编译，零注解处理器；Gradle 侧退位为 schema 变更时的再生成工具。

## 期待的新实现

- Gradle 构建产物中的 Room 生成类复制入 `src/main/java` 对应包路径，与手写源码同等编译
- 新增脚本（tools/ 下）做「再生成同步」：本地 Gradle 跑一次 kapt，diff 生成目录与 check-in 目录，提示新增、变更与孤儿文件
- CI 增加一致性检查 job：Gradle 再生成后工作区无 diff，防止 schema 改了忘同步
- 文档注明 schema 变更流程：改实体 → 本地再生成 → 连同生成类一并提交

## 作用域

- `src/main/java` 新增的生成类文件
- `tools/room_codegen_sync.*` 再生成同步脚本
- ci 目录新增一致性 job
- 步骤 2 迁入的三个实体同样纳入此机制

## 验证

- 删除本地 build 目录后 Gradle 构建通过，证明编译只依赖 check-in 生成类而非重新生成
- Soong 侧 spike：仅含 check-in 生成类与 Room runtime 源码时模块可编

## 树内基建备注（LOS 23.2 静态验证）

- SystemUI 在树内声明 `plugins: ["androidx.room_room-compiler-plugin"]`，Room 编译器存在 Soong 插件通路，但仅覆盖 javac 源集；Operit 实体为 Kotlin 依赖 kapt，不走此通路，check-in 方案维持
- 若后续接受实体转 Java，可改用树内插件方案替代 check-in，两方案不共存

## 风险

- Room 升版本时生成类引用的 runtime API 可能变化，同步脚本与 CI job 是唯一防线
- 需固定 Room 版本并在清单文档登记，升级需走再生成流程
