---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: 待建（依赖 fork 仓就位后动工）
Status: 方案定稿待树侧资源
---

# ripgrep 树内源码线（翻 2026-09-22 prebuilt 定案）

## 原有状况

`liboperit_ripgrep`（rg 内容搜索 JNI 桥，Rust cdylib）为 prebuilt 本位：

- 源在 `tools/native_ripgrep/`（globset/grep-matcher/grep-regex/ignore/jni/serde 系依赖）
- android-build workflow cargo 步骤交叉编译 → 回写 `app/src/main/jniLibs/arm64-v8a/`
- 树内侧 `app/src/main/jniLibs/Android.bp` 以 `cc_prebuilt_library_shared` 消费同一 .so
- 2026-09-22 定案：「树内 crates 无对应，故不走源码线」

## 意图

翻案：ripgrep 走树内源码线，prebuilt 退役——对齐 ffmpeg/provider 的 fork 模式，
蹭树编译一致性红利，消掉 CI cargo 交叉编译与 .so 回写这条 Gradle 独有脐带
（Gradle 线仍需保留该闭环，见非目标）。

## 方案

分两宿，职责最简：

- **fork 仓 `android_external_ripgrep`（组织仓，待建）**：纯依赖仓。vendored
  ripgrep workspace 相关 crate（globset/grep-regex/grep-matcher/ignore 及传递依赖）
  + jni/serde/serde_json 钉版本，各配 `rust_library` Android.bp，
  visibility 开 `packages/apps/Operit`。不赌树内 external/rust/crates 版本。
- **本仓**：`Android.bp` 增 `rust_library_dylib liboperit_ripgrep`（srcs 指
  `tools/native_ripgrep/src/lib.rs`，rustlibs 全指 fork 仓模块）；删
  `app/src/main/jniLibs/Android.bp` 的 cc_prebuilt（模块名让位）。
  wrapper 源单宿本仓，与 Gradle cargo 线共用同一份 `src/` 与 `Cargo.toml`。

## 步骤

1. [fork 仓 vendoring 与 Android.bp 模块群](1_ForkRepoVendoring.md)
2. [本仓翻牌与树侧验证 runbook](2_InRepoFlipAndRunbook.md)

## 非目标

- Gradle 线不动：jniLibs .so 与 CI cargo 闭环继续伺候 assembleDebug/nightly
- 不动其他四个 native 模块（quickjs/wamr/streamnative/sherpa 各有形态）

## 验证

- 树侧：`m liboperit_ripgrep` 产物落位；`m Operit` 全绿（jni_libs 解析到源码模块）
- 产物比对：源码线 .so 与 cargo 线 .so 导出符号面一致（JNICALL 计数）
- 真机：rg 内容搜索工具走查一遍
