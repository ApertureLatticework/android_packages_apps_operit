---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: feat/live-pipeline-completion
Status: 计划中——方案细化完成待用户过目后动工
---

# Live 三缺件：帧泵实时流、流式多模态通路、AST 查询工具化

## 原有状况

Live 线随 aosp_native_priv_integration 步骤 6/7 落了骨架，但闭环缺三件：

- `LiveScreenMirror` 仅有按需 `captureFrame()` 单帧接口，无连续帧流；消费方
  （PrivilegedUITools 截屏、capture_screenshot 工具）每次取帧都要等首合成周期
- `PhoneAgent` 决策循环是纯文本单发：每步只带文字观察，看屏必须先手动截屏存文件
  再走文件引用，多模态模型无法直接消费帧
- 语义树查询只服务无障碍执行定位：`UIHierarchyManager` 的 `getUiHierarchy()` 返回
  全量 XML，无按条件查询能力，Agent 无法以工具形式精准定位节点（只能全量拉取再肉眼解析）

## 意图

补齐 Live 自动化闭环的三个缺口，形成"连续看屏 → 流式决策 → 语义树定位执行"的完整链路。

## 期待的新实现

1. 帧泵实时流：LiveScreenMirror 升级为定频连续帧泵，向订阅者推送最新帧流，
   背压策略为最新帧优先、旧帧即弃
2. 流式多模态通路：PhoneAgent 每步自动携带最新帧作为 image part 发往云端
   多模态 Provider，复用现有 image_url 通道，决策与观察同流
3. AST 查询工具化：语义树以查询工具暴露给 Agent，支持按文本/id/类名/可点击性
   筛选节点，返回结构化节点句柄，直通 setTextOnNode 等精操

## 作用域

- `core/tools/system/live/`：帧泵
- `core/tools/agent/`：PhoneAgent 决策循环多模态化
- `data/repository/UIHierarchyManager.kt` + 新增查询工具：AST 工具化
- 工具注册与提示词同步

## 非目标

- 不动 provider APK 本体（源码入仓另行专项，见 ../accessibility_provider_sourcing_20260923/）
- 不做本地模型推理（mnn/llama 已随步骤 4 切割出仓）
- 不引入新的第三方依赖

## 步骤

1. [帧泵实时流](1_FramePump.md)（方案就绪待过目）
2. [流式多模态通路](2_StreamingMultimodal.md)（方案就绪待过目）
3. [AST 查询工具化](3_AstQueryTool.md)（方案就绪待过目）

## 执行情况（2026-09-23，分支 feat/live-pipeline-completion，基于 lineage-23.2）

步骤 1（帧泵）：
- LiveScreenMirror 增泵层：OnImageAvailableListener（专用 HandlerThread）事件驱动，
  节流窗口（liveFramePumpIntervalMs，0/250/500/1000 四档，默认 250），
  MutableSharedFlow(replay=0, DROP_OLDEST) conflate 语义；无订阅者不复制 Bitmap
- 新接口：`frames: SharedFlow<Frame>`、`awaitFreshFrame(afterSequence)`（静默画面
  超时返回 null 显式信号）、`latestSequence()`、`applyPumpIntervalMs()`（设置面即时生效）
- captureFrame/captureLatestBitmap 按需接口原样保留；设置项入 GlobalDisplaySettingsScreen
  （FilterChip 四档），八语同步

步骤 2（流式多模态）：
- captureScreenshotForAgent 泵化：首帧轮询取当前画面，此后 awaitFreshFrame 等
  稳定新帧；画面未变时复用上帧 imageId 并以 "[SCREENSHOT] Screen unchanged..."
  显式告知模型（不冒充新帧）
- 历史帧双帧保留：recentFrameImageIds 上限 2，被逐出者从 prompt 历史与
  ImagePoolManager 池同步清除；removeImagesFromLastUserMessage 整删
- 遮罩常隐藏：特权主屏会话期间指示器/进度遮罩不再逐帧显隐（run 级隐藏、finally 恢复），
  消除 delay(200) 逐帧开销与泵帧污染；非泵路径（副屏/非特权）保留原遮罩

步骤 3（AST 查询工具化）：
- query_ui_tree 落地：XmlPullParser 流式遍历（不建 DOM），text_contains/
  resource_id（后缀匹配）/class_name/clickable 多条件 AND，max_depth/max_results
  截断带 truncated+hint；返回 nodeId 与 setTextOnNode 同源闭环
- 注册三件套：ToolRegistration executor、SystemToolPromptsInternal 双语
  ToolPrompt、toolreg 描述字符串（values/en，跟随 toolreg 前例）

验证：检Localization 门禁零错；待 CI 编译与真机四场景回归（步骤 9 清单）。
