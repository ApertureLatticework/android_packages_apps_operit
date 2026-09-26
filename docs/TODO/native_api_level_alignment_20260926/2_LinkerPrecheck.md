# 2. linker driver fail-fast 预检 [DONE]

## 旧实现情况

四处同构 cargo 步（build×1 / pr-check×2 / tests×1）export
`CARGO_TARGET_AARCH64_LINUX_ANDROID_LINKER` 后直接 rustup/cargo；linker 缺失时
只能等 cargo 抛晦涩 linker 报错，且此前 ripgrep cache restore 成本已付。

ps1 侧早有 `Test-Path` + `throw` 语义（build_native_ripgrep.ps1），CI 侧没有对等物。

## 意图修正

对齐 ps1 的 fail-fast 语义：缺 driver 即 `::error::` 标注 NDK 版本/API/完整路径并退出。
预检还应独立成步、位次紧随 toolchain 安装——先于一切缓存恢复/rustup/cargo。

## 新实现情况

- 91ec9c8：四处 cargo 步内联 `-x` 存在性检查
- a59687a：内联块上移为独立「NDK linker driver 预检」步（fail-fast 语义补全），
  并以同款注释显式声明：CI 只面向 arm64-v8a 单 ABI（jniLibs 与 ffmpeg jni 均只发
  arm64-v8a，单 linker 预检为有意设计）；未来扩多 ABI 时同步扩预检清单

## 验证

四份 workflow 的预检步位次（toolchain 安装后、首个 cache restore 前）与注释一致性
人审；缺 driver 的红路径由注释中 `::error::` 格式保证。
