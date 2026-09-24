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
