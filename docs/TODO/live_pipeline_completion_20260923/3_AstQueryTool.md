# 3. AST 查询工具化 [DONE]

## 旧实现情况

语义树数据源已通但无查询能力：

- provider APK 的 `UIAccessibilityService` 经 AIDL 暴露 `getUiHierarchy()`，返回
  uiautomator 风格全量 XML（node 属性含 package/text/resource-id/class/clickable/bounds）
- 主应用侧 `UIHierarchyManager.getUIHierarchy()` 全量拉取；`extractWindowInfo` 只挖包名
- 精操接口已有：`setTextOnNode(nodeId, text)`、`findFocusedNodeId()`，但 Agent 无从得知
  nodeId——全量 XML 太大塞 prompt，塞了也要模型肉眼找
- PhoneAgent 决策纯靠截图视觉定位，语义树信息未参与

## 意图

语义树以查询工具暴露：Agent 按条件筛选节点，拿到结构化句柄后直通精操，
视觉定位与语义定位互补。

## 期待的新实现

新工具 `query_ui_tree`（注册进特权档工具链，AccessibilityUITools 谱系）：

```
query_ui_tree(
    text_contains  : string?   -- 文本模糊匹配（含 content-desc）
    resource_id    : string?   -- 精确或后缀匹配（com.app:id/xxx 的 xxx）
    class_name     : string?   -- 类名后缀匹配（TextView / EditText）
    clickable      : bool?     -- 可点击性过滤
    max_depth      : int = 24
    max_results    : int = 20
) -> JSON 数组，每项：
   { nodeId, text, contentDesc, className, resourceId,
     bounds: {left, top, right, bottom}, clickable, focusable, depth }
```

实现要点：

- XML 拉取一次（`getUIHierarchyWithRetry` 复用），XmlPullParser 流式遍历，
  条件全部在遍历中判定，不建全量 DOM
- nodeId 沿 provider 现有编号语义（`findFocusedNodeId`/`setTextOnNode` 同源），
  查询结果 nodeId 可直接喂 `setTextOnNode`
- 结果超 `max_results` 截断并附 `truncated: true` 与总数，提示模型收窄条件
- 一次查询多条件为 AND；无结果返回空数组，不报错
- 提示词：SystemToolPromptsInternal 增条目，示例覆盖"找输入框拿 nodeId →
  setTextOnNode"链路；与截图工具互相引用（语义找不到时换视觉）

## 作用域

- `core/tools/defaultTool/accessbility/AccessibilityUITools.kt`：新执行器
- `data/model/AITool` 声明与参数 schema
- `core/config/SystemToolPromptsInternal.kt`：提示词（中英）
- 八语字符串若涉及 UI 暴露面则同步（纯 Agent 工具则零 UI 面）

## 验证

- 副屏会话：query_ui_tree(text_contains="登录") 返回可点击节点，nodeId 喂
  setTextOnNode 成功输入
- 大树（设置应用主页）查询耗时 < 150ms，截断生效
- 全条件组合回归：空结果、单条件、多条件 AND
