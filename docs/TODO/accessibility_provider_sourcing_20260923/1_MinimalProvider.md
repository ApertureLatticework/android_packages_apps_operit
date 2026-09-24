# 1. 最小 provider 工程与 AIDL 实现 [DONE]

## 旧实现情况

provider 行为只能从 APK 反推：UIAccessibilityService 实现 AIDL 十方法，
RemoteBinderService 交付 Binder，Compose 壳占 95% 体积。

## 意图

零 androidx 的最小源码工程，行为契约 = 主仓 `IAccessibilityProvider.aidl` 逐字面。

## 期待的新实现

目录布局（本仓根 `provider/`）：

```
provider/
├── Android.bp
├── AndroidManifest.xml
└── src/com/ai/assistance/operit/provider/
    ├── UIAccessibilityService.kt
    ├── RemoteBinderService.kt
    └── IAccessibilityProvider.aidl   （自主仓复制，包内共享源）
```

- `UIAccessibilityService`：
  - getUiHierarchy：rootInActiveWindow 起 AccessibilityNodeInfo 深度遍历，
    序列化为与现 APK 同构的 node XML（属性集 package/class/text/resource-id/
    content-desc/clickable/focusable/scrollable/bounds + nodeId）；
    跨窗口遍历 windows（API 21+）保多窗口场景
  - nodeId：AccessibilityNodeInfo 身份哈希（与 findFocusedNodeId 同源），
    setTextOnNode 以遍历重找节点回写（ACTION_SET_TEXT）
  - performClick/LongPress：坐标换算根节点后 dispatchGesture；
    performSwipe/performGlobalAction 直译
  - takeScreenshot(path, format)：AccessibilityService.takeScreenshot 异步转同步，
    Bitmap 落盘 JPEG/PNG
  - getCurrentActivityName：rootInActiveWindow.packageName + window title 事件维护
- `RemoteBinderService`：onBind 回传 UIAccessibilityService 持有的 Binder 桩
- manifest：无 activity，service 两枚（accessibility 主服务带
  `android.permission.BIND_ACCESSIBILITY_SERVICE` intent-filter + meta-data 指 xml 配置；
  binder 服务带自定义 action `com.ai.assistance.operit.provider.IAccessibilityProvider`），
  `android:sharedUserId` 不设，独立进程跑无障碍
- service 配置 xml：canRetrieveWindowContent、canPerformGestures、
  canTakeScreenshot、accessibilityEventTypes/FeedbackFlags/NotificationTimeout 与
  现 APK 对齐（feedbackGeneric、typeWindowStateChanged）
- Android.bp：`android_app { name: "OperitProvider", platform_apis: true,
  certificate: "platform", sdk_version: "" }`，kotlinc 走树内 Kotlin

## 验证

- 树侧 `m OperitProvider` 产物 < 100KB，无 androidx 依赖残留（apkanalyzer 清单）
- 四场景之"无障碍 provider 自动登记"改由本模块承载，行为不回退
- UIHierarchyManager 全量回归：getUIHierarchy/performClick/setTextOnNode/
  takeScreenshot/事件回调逐项过
