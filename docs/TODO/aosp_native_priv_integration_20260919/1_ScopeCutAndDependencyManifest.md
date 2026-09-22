# 1. 功能面裁剪与依赖清单定稿

## 旧实现情况

`app/build.gradle.kts` 声明约 120 条 Maven 依赖，覆盖 Agent 核心与外围功能 indiscriminately：

- 核心线：Compose 全家、协程、okhttp、kotlinx-serialization、DataStore、mnn、llama、quickjs、ffmpeg、onnxruntime
- 借权线：libsu、shizuku-api、shizuku-provider
- 外围线：Tasker 插件、POI 三件套、iTextG、PDFBox、zip4j、ExoPlayer、Glide、Coil、Markwon 系、Liquid/Backdrop/ColorPicker 等主题件、MediaPipe、hnswlib 双轨

## 意图

ROM 版只保留核心功能面（Agent、Live 自动化、文件与工作区），外围功能整块裁掉，产出一份入树依赖白名单，把需要源码进树的 Java 库数量压到最小。

首个也是最大的一刀：Linux 终端环境整线裁剪——proot、chroot、lxc 全部不入 ROM 版，不写 root helper。裁剪面：

- 终端引擎模块（:terminal，subpack 来源的 termux 系引擎）与 proot 二进制
- Ubuntu rootfs 装载链：ubuntu-*.tar.xz、proot-distro 布局、PathMapper 的 linux↔android 映射
- shell 工具 environment="linux" 参数分支删除（提示词文案的全面精简独立为步骤 8，放最后）
- MCP 本地 node 侧：MCPDeployer、MCPStarter、MCPBridge 的 proot 内部署与 ~/bridge；远程 MCP 客户端保留
- SSH/SFTP 服务与终端 UI 入口、引导
- PerformanceMonitorManager 的 bash→proot PTY 监控
- 清理所有引用 environment="linux" 的工具与 ToolPkg，不留死参数

UI 面同步裁剪（设置页、向导、导航）：

- 导航与路由：ScreenRouteRegistry 的 toolbox.terminal 路由与 Screen.Terminal 注册、NavItem.Terminal 抽屉入口
- 工具箱终端页与其入口，ShellExecutorScreen 的 linux 环境选项
- 向导卡片：OperitTerminalWizardCard 整张删除（连同 Root/Shizuku 卡片归步骤 4）
- 演示与状态：DemoStateManager 预置终端命令、PermissionLevelCard 的 feature_termux_support 条目
- PerformanceMonitorScreen 的 PTY 监控展示区
- 工作区（保留功能）仅清理 Terminal 图标与文案引用

## 期待的新实现

- 在本步骤文档结尾附依赖三分类清单：
    - 入树源码：随包编进 libs/
    - 随功能裁掉：对应源码目录与入口一并删除
    - 唯一例外：ML Kit prebuilt
- 每条裁掉的依赖给出它支撑的功能与对应删除面，避免删依赖留死代码
- 清单定稿后步骤 5 以其为唯一输入

## 作用域

- `app/build.gradle.kts` 依赖区
- 被裁功能的 `ui/features/` 对应界面、`core/tools/` 对应工具入口
- `gradle/libs.versions.toml` 同步清理

## 依赖三分类清单 v1

坐标基线：libs.versions.toml 119 条 + build.gradle.kts 直接坐标约 15 条。使用面积均经 rg 实测。

### 入树源码

| 簇 | 依赖 |
| --- | --- |
| Compose 与 androidx | compose 全家（ui/material3/icons-extended/animation/navigation/material3-window）、appcompat、material、core-ktx、activity-ktx、lifecycle、window、webkit、glance 双条 |
| 数据与序列化 | room 双条、datastore 双条、coroutines 双条、kotlinx-serialization、kotlin-reflect、gson、hjson、uuid |
| 网络 | okhttp 三条、ktor-client-okhttp、mcp-sdk-client、jsoup |
| 图片与显示 | coil 三条（33 文件在用）、androidsvg、android-gif、jlatexmath（6 文件） |
| Agent 工具面 | zxing、java-diff-utils、commons-compress、commons-io、zip4j（2 文件）、apksig、apk-parser、sable-axml、zipalign-java |
| 记忆与向量 | jieba、hnswlib 双条、tensorflow-lite、mediapipe-tasks-text、onnxruntime |
| 安全与本地服务 | security-crypto、bcprov-jdk18on、nanohttpd |
| UI 组件 | colorpicker、backdrop、liquid、reorderable、swipe、image-cropper |

### 随功能裁掉

| 依赖 | 支撑功能 | 面积 |
| --- | --- | --- |
| libsu 三条、shizuku 双条 | root 与 Shizuku 借权 | 归步骤 4 |
| objectbox 双条与插件 | 记忆向量旧存储 | 归步骤 2 |
| retrofit 双条、moshi-kotlin | 无引用死依赖 | 零文件引用，直接删 |
| glide | 无引用死依赖 | 零文件引用，与 coil 双轨 |
| poi 三条、itextg、pdfbox、junrar | 改判保留 | Agent 文件工具与工作区文档预览在用（StandardFileSystemTools、WorkspaceReadOnlyDocumentPreview），属文件与工作区核心域 |
| exoplayer 三条 | 改判保留 | 见待定区定案，Soong 化时迁 androidx.media3 |
| renderx | 改判保留 | 聊天 markdown 流渲染链在用（CanvasMarkdownNodeRenderer 等 4 文件） |
| taskerpluginlibrary | Tasker 集成线（已执行） | 删除 integrations/tasker、manifest 三条目、trigger_tasker_event 工具与双语 schema、JS 桥、工作流 tasker 触发类型与引擎函数；WorkflowBootReceiver 迁居 integrations/intent 保留 |
| desugar-jdk | Gradle 构建期概念 | 平台构建无需，不入树 |

### 唯一例外（prebuilt AAR）

- mlkit text-recognition 五条（默认、中、日、韩、天城文）

### 测试基建（不随 app 入树）

- junit、androidx-junit、espresso、runner、rules、ui-test-junit4、mockito 三条、coroutines-test、json-jvm、compose-ui-tooling 与 test-manifest
- Soong 侧有对应机制，后续按 android_test 单独规划

### 模块级与资产级

- 保留：:quickjs、:mnn、:llama、assets/accessibility.apk（无障碍 provider，phone use 核心）
- 裁掉：:terminal、assets/shizuku.apk（Shizuku 引导安装）
- 待定：avatar 线四模块与 filament 三条坐标（:dragonbones、:mmd、:fbx、:showerclient）及 assets 侧 desktop.apk、pets、dragonbones 目录

### 待定区定案（2026-09-19 确认）

| 簇 | 结论 |
| --- | --- |
| avatar 线（四模块 + filament + 相关资产） | 裁 |
| exoplayer | ✅ 已迁移 androidx.media3 1.8.0（2026-09-22：9 Kotlin + 1 XML + Gradle 三条依赖；树内走 androidx prebuilts） |
| glance 小部件 | 保留 |

### 统计

- 裁后入树 Java 坐标约 60 条，其中 androidx 系约半数
- 死依赖两条（retrofit、glide）先行清理，属无风险首刀（已完成：build.gradle.kts 与 toml 同步删除，代码零引用无需改动）

### terminal 线执行地图（2026-09-19 已执行）

架构事实：

- 终端引擎是独立子模块：terminal 为 git submodule（OperitTerminalCore），引擎本体又是独立伴侣 App（com.ai.assistance.operit.terminal，OperitTerminalManager 负责检测安装）
- Agent 的终端会话工具族全部经 Terminal 单例跑在终端引擎上；shell 工具的 android 环境走权限阶梯，不受影响
- FileSystemProvider 与 SSH 客户端栈均在终端子模块内，SSH 远程文件能力随线裁撤，未来可用纯 Java SSH 库重加

已执行：

- 子模块 gitlink 与 .gitmodules 条目、settings.gradle 与 build.gradle 引用全部移除
- Terminal.kt、OperitTerminalManager.kt、StandardTerminalCommandExecutor.kt、LinuxFileSystemTools.kt 删除
- StandardFileSystemTools 的 17 处 linux 委托与跨环境复制的 linux 分支改写为纯 Android/repo 语义
- MCP：MCPStarter 重写为 remote-only，MCPBridge/MCPBridgeClient/MCPDeployer/MCPSharedSession/BridgeMcpRuntimeSession 删除，McpRuntimeDescriptor.Local 与 MCPRepository 的本地链路同步移除，远程 MCP 保留
- UI：ComputerScreen 删除、ChatViewModel 工作区终端命令链路与 AI 电脑开关删除、WorkspaceCommandExecutionDialog 删除、ToolboxScreen 终端两屏删除、OperitScreens 三屏与路由/NavItem 清理、向导卡删除、PerformanceMonitor 终端实体链路删除
- 工具与提示词：五个终端工具注册、双语 schema、JS 桥、PathMapper 消费方清理
- 字体：CanvasCodeEditorView 与 MarkdownCodeTypeface 改用系统等宽字体

遗留（归步骤 8 或后续清扫）：

- 提示词中 environment="linux" 文案与 copy_file 的 source/dest_environment 描述
- 演示页的 isOperitTerminalInstalled 等参数名与终端环境卡片（恒 false 展示）
- PathMapper 不可达的 linux 分支；MCPConfigScreen 本地插件添加表单
- 孤儿字符串（终端工具、向导、perf_kind_terminal 等）

### avatar 线裁剪作战清单（rg 实测，2026-09-19 已执行）

- Gradle：settings.gradle 移除 :dragonbones、:mmd、:fbx 三条与 avator/ 目录映射；app/build.gradle.kts 移除三个 project 依赖与 filament 三条坐标
- 源码：core/avatar 35 文件整目录、ui/features/assistant 下 avatar 三组件与 Screen/VM、ui/floating/ui/pet、ui/components/ManagedDragonBonesView、data/model/DragonBones.kt、data/repository/AvatarRepository 与 AvatarConfigPersistence、ConversationService 的 mood 注入、FunctionalPrompts 的 avatarMoodRulesText、悬浮全屏的 voiceAvatar 状态机与 AvatarView 区、AssistantConfig 路由与 NavItem、ChatViewModel 的 mood 标签剥除
- 资产：assets 下 pets 与 dragonbones 目录
- 判定修正：shower 线不是 avatar 线，它是 PhoneAgent 的现有副屏后端，与原生副屏的切换归步骤 7 原子执行；showerclient 模块、Shower* 文件、desktop.apk、autoglm 工具页本次全部保留
- 遗留：values 及各 locale 的孤儿字符串（avatar 配置、pet、mood）随步骤 8 的 i18n 清理批量处理
