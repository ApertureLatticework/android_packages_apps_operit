---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: feat/accessibility-provider-opensource
Status: 调查完成——上游无源码，方案定稿为参考 AOSP 最小重写，待动工
---

# accessibility provider 源码入仓

## 原有状况

无障碍执行链路依赖伴随 APK：

- `app/src/main/assets/accessibility.apk`（2.7MB，v1.5）为纯二进制，源码不在任何可及仓库
- 上游 AAswordman/Operit 已实证无 provider 工程：settings.gradle.kts 无对应 module，
  .gitmodules 仅 terminal 与 NightlyRelease 两项
- 解包实证：自有代码仅 4 类——MyApplication、MainComposeActivity、
  RemoteBinderService、UIAccessibilityService；其余体积全是 Compose/Material 运行时模板
- 主应用侧接口完备：AIDL `IAccessibilityProvider`（10 方法：getUiHierarchy、
  performClick/LongPress/Swipe/GlobalAction、findFocusedNodeId、setTextOnNode、
  takeScreenshot、isAccessibilityServiceEnabled、getCurrentActivityName）
  与 `IAccessibilityEventCallback` 均在主仓 aidl/ 目录
- 消费端 `UIHierarchyManager` 绑定安装、`AccessibilityProviderInstaller` 负责
  assets 提取安装与版本校验更新

## 意图

provider 源码入仓并 Soong 化，消灭 2.7MB 二进制与安装器链路，随 ROM 直接分发。

## 期待的新实现

参考 AOSP 重写为最小工程（4 类 + manifest + service 配置 xml，纯 framework API，
零 androidx 依赖，产物 < 100KB）：

- `packages/apps/Operit/provider/`（本仓 `provider/` 目录）：
  - `UIAccessibilityService`：AccessibilityService 子类，实现 AIDL 十方法。
    getUiHierarchy 走 AccessibilityNodeInfo 树序列化（nodeId 用
    AccessibilityNodeInfo.getIdentityHashCode 语义对齐 provider 现行为，
    setTextOnNode 消费同源）；手势走 GestureDescription；takeScreenshot 走
    AccessibilityService.takeScreenshot API（API 30+，ROM 最低线之上）
  - `RemoteBinderService`：回传 IAccessibilityProvider Binder 的轻服务
  - MainComposeActivity 与 MyApplication 删除：provider 无 UI 面，安装即用
- AOSP 参考线：frameworks/base AccessibilityService/Jetpack 无关系；
  序列化格式对齐 uiautomator（AccessibilityNodeInfo#dump 谱系），主仓
  UIHierarchyManager 的 XmlPullParser 消费面零改动
- Soong 模块 `OperitProvider`（android_app，platform 签名，与主 APK 同标签分发）
- 主应用侧接线：AccessibilityProviderInstaller 与 assets 提取安装链整删，
  UIHierarchyManager 的绑定目标改为同签名直装包；accessibility_version.txt 废除，
  版本以模块构建号为准

## 作用域

- 新增 `provider/` 源码工程与 Android.bp
- `AccessibilityProviderInstaller.kt` 删除、`UIHierarchyManager.kt` 接线收敛
- `app/src/main/assets/accessibility.apk` 与 `accessibility_version.txt` 删除
- Android.bp jni_libs/assets 打包面同步

## 非目标

- 不保留 Gradle 侧 provider 构建（ROM-only 分发，Gradle 线不再消费该 APK）
- 不做 provider 与主应用同进程合并（无障碍服务独立进程是规避主进程被杀连坐的既有设计）

## 步骤

1. [最小 provider 工程与 AIDL 实现](1_MinimalProvider.md)
2. [主应用接线收敛与安装器删除](2_ClientRewire.md)

## 决策记录

| 决策点 | 结论 |
| --- | --- |
| 源码来源 | 上游无（实证）；参考 AOSP AccessibilityService API 重写，行为对齐现有 AIDL 契约 |
| UI 壳 | MainComposeActivity 删除，provider 零 UI |
| 分发 | OperitProvider 模块随 ROM 分发，platform 签名 |
| nodeId 语义 | AccessibilityNodeInfo 身份哈希，与 setTextOnNode 同源闭环 |

## 执行情况（2026-09-23，分支 feat/accessibility-provider-opensource）

步骤 1（最小 provider 工程）：
- provider/ 新模块：Android.bp（android_app `OperitProvider`，platform 签名，
  minSdk 30，零 androidx）+ manifest（无 activity，双 service）+
  res/xml 服务配置（canRetrieveWindowContent/canPerformGestures/canTakeScreenshot）
- UIAccessibilityService：十方法全实现——uiautomator 风格 XML 序列化（nodeId 为
  节点身份哈希，findFocusedNodeId/setTextOnNode 同源闭环）、GestureDescription
  坐标手势、takeScreenshot API 30+ 硬件位图转存；IAccessibilityEventCallback
  死接口不迁移
- RemoteBinderService：onServiceConnected 时挂桩，onBind 交付

步骤 2（主应用接线收敛）：
- AccessibilityProviderInstaller.kt、assets/accessibility.apk（2.7MB）、
  accessibility_version.txt、IAccessibilityEventCallback.aidl、
  AccessibilityEvent.aidl 全删；主 APK -2.7MB
- UIHierarchyManager：安装线四法删除，绑定直钉 RemoteBinderService 组件
  （ROM 内建，不经包管理器发现）
- AccessibilityAutoEnable：登记目标修正为直钉 UIAccessibilityService——
  原按 action 解析会误中 RemoteBinderService（组件语义错位，一并根除）
- DemoStateManager：provider 安装状态卡删除（ROM 恒在场）
- 字符串：accessibility_provider_unknown/installed 孤儿键八语清除，门禁零错
- 树侧接线：device.mk 增 `PRODUCT_PACKAGES += OperitProvider`（runbook 已更新）

待树内验证：`m OperitProvider` 产物 < 100KB；四场景之"无障碍 provider 自动登记"
由本模块承载；UIHierarchyManager 全量回归（getUIHierarchy/performClick/
setTextOnNode/takeScreenshot 逐项）。

## 树内验证（2026-09-24，dodge 树 m Operit OperitProvider 全绿）

- 环境：LOS 23.2（cnb.cool 镜像）+ 一加 13（dodge/sm8750），64C128G 云机
- ffmpeg：实际生效 7b7f4a3（定制 4）——e6285e1 只含定制 1，libavutil 仍依赖
  libudev 挡 Soong 解析；local_manifests 钉子已同步更新（b21710f）
- 产物：Operit.apk 87.2MB @ system/priv-app/；OperitProvider.apk 2.04MB
  @ system/app/（体积实测修正原 <100KB 预期：kotlin-stdlib 打包所致，合理）
- 途中修复：provider manifest 组件声明补 application 包裹（8a83a97，aapt2
  manifest_fixer 严格校验）
- 源码树内依赖教训（非本仓问题，记录备查）：1.sh 依赖列表缺 zip（genrule
  sbox 调 zip 打 srcjar）；新 shell 必须 breakfast 设目标环境后才能 m
