# 1. 帧泵实时流 [DONE]

## 旧实现情况

`LiveScreenMirror` 只有拉取式接口：

- `captureLatestBitmap()`：`acquireLatestImage()` 单帧取走，无新帧返回 null
- `captureFrame(maxWaitMs)`：短轮询等首帧，供 PrivilegedUITools 与 PhoneAgent 每步调用
- 消费方每次取帧都要承担完整链路：隐藏悬浮窗 → delay(200) → 取帧 → 压缩存盘 → 生成 imageId link
- 无事件驱动：画面变化不通知任何人，想看变化只能反复拉取

## 意图

镜像通道升级为持续帧泵：画面变化即产出帧事件，订阅方按需取用，为流式多模态
（步骤 2）提供"决策期间画面持续更新"的数据源。

## 期待的新实现

`LiveScreenMirror` 增帧泵层，不动引用计数与按需接口：

```
ImageReader.setOnImageAvailableListener(handler = 泵专用单线程 Handler)
    → 节流窗口（minIntervalMs，默认 250ms，可配）
    → Bitmap 复制（沿现 copyPixelsFromBuffer + 行距裁剪逻辑）
    → MutableSharedFlow<Frame>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = DROP_OLDEST)
       即 conflate 语义：慢订阅者永远拿到最新帧，旧帧即弃

data class Frame(
    val bitmap: Bitmap,        // 全分辨率 ARGB_8888，消费方负责 recycle
    val sequence: Long,        // 泵内单调递增
    val timestampMs: Long      // SystemClock.elapsedRealtime
)
```

- 泵随镜像显示器生命周期启停：createMirror 成功即挂 listener，teardown 即撤；
  无订阅者时 listener 仍工作但 Bitmap 立即丢弃（保持画面新鲜度语义），代价可控
- 保留 `captureFrame()` 按需接口不改签名：PrivilegedUITools 截屏工具路径不变
- 新增 `val frames: SharedFlow<Frame>` 与 `suspend fun awaitFreshFrame(afterSequence: Long): Frame?`：
  等待比自己手中序号新的帧（静默画面下 AUTO_MIRROR 不发帧，超时返回 null，
  调用方显式处理"画面未变化"，不得静默重发旧帧冒充新帧）
- 帧泵不负责降采样与编码：消费方按各自需求缩放（多模态缩图、截屏工具原图）

## 作用域

- `core/tools/system/live/LiveScreenMirror.kt` 单文件为主
- 显示设置新增帧泵频率项（`DisplayPreferencesManager`，默认 250ms，四档：100/250/500/1000）
- 八语字符串同步

## 验证

- 单元层：泵 conflate 语义——慢消费方连收两帧时序号跳变正确
- 集成层：PhoneAgent 步间执行动作后 `awaitFreshFrame(oldSeq)` 能在 2s 内拿到新帧；
  静态桌面（无变化）时按超时返回 null 而非阻塞
- 资源层：dumpsys meminfo 连续 10 分钟泵运行无 Bitmap 泄漏
