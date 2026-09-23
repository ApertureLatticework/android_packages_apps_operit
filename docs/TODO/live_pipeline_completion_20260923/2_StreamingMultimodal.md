# 2. 流式多模态通路

## 旧实现情况

PhoneAgent 决策循环（`_executeStep`）的看屏链路：

- 每步 `captureScreenshotForAgent()`：隐藏悬浮窗 → delay(200) → `LiveScreenMirror.captureFrame()`
  → 压缩存盘 → `ImagePoolManager.addImageFromBitmap` 拿 imageId → 拼
  `<link type="image" id="...">` 进 user message
- 模型响应走 `uiService.sendMessage(stream = true)` 已是流式收集
- 每步结束 `removeImagesFromLastUserMessage` 把上一帧从历史剥除，模型永远只见当帧

## 意图

决策循环消费帧泵（步骤 1 产物），帧获取从"每步全链路拉取"改为"等稳定帧即取"，
多模态输入与流式输出在同一步内闭环，并为模型保留画面变化的前后帧对照。

## 期待的新实现

- `_executeStep` 取帧改走帧泵：持上一步序号 `lastSequence`，执行动作后
  `awaitFreshFrame(lastSequence)` 取新帧；取不到（画面未变）时明确告知模型
  "画面与上一步相同"并复用上帧 imageId
- 稳定帧判定：动作执行后连续两次取帧序号一致或 300ms 静默即视为稳定，
  取代固定 delay(200) 遮罩等待
- 浮浮窗遮罩语义收敛：帧泵常开后悬浮窗绘制进镜像画面会污染帧，改为泵订阅期间
  状态指示器常隐藏（Live 会话已由 LiveService 宿主，不再每步显隐抖动）
- 历史帧保留：`removeImagesFromLastUserMessage` 调用删除，保留最近两帧
  （上帧 + 当帧）在上下文中，模型可对照画面变化；更早帧照旧剥除
- imageId 生成走内存：`ImagePoolManager` 现有池即内存池，压缩参数沿现值不动；
  磁盘持久化路径仅截屏工具继续用

## 作用域

- `core/tools/agent/PhoneAgent.kt`：`_executeStep`、`captureScreenshotForAgent`
- `core/tools/agent/PhoneAgent.kt` ActionHandler：显隐控制、历史帧剥除
- 不动 Provider 层：image part 编码沿用 `toPromptTurns` 现有通路

## 验证

- 主屏特权档 Live 会话：连续 5 步任务，每步 prompt 携带当帧 imageId，
  模型响应可引用画面变化
- 帧序号进日志：动作后到新帧的等待时长 ≤ 700ms（对比旧链路 200ms 遮罩 + 压缩 IO）
- 上下文 token 面：保留双帧后 prompt 膨胀可控（imageId link 为短文本）
