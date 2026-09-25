---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: 待建（依赖 fork 仓就位后动工）
Status: 已落地（2026-09-26 树侧 m liboperit_ripgrep + m Operit 全绿，见文末）
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

- **fork 仓 `android_external_ripgrep`（已备内容，待组织仓开权限）**：闭包 35
  件对账 AOSP android-crates-io 后仅 vendor 7 件（globset/grep 三件/ignore +
  regex 断代三件），jni 0.21.1 与 serde 系树内同线直接借用。仓内容已就绪于
  本地（7 crate 瘦身源 + Android.bp + README），等组织仓建立即推。
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


## 次日树侧首验清单（2026-09-26）

前置：fork 仓名已定正 android_external_ripgrep（manifest 045d7f6 已同步）。

```bash
cd <LOS 23.2 树根>
cp <local_manifests>/upstream-forks.xml .repo/local_manifests/   # 若未装
repo sync -c -j8 external/operit-ripgrep packages/apps/Operit
source build/envsetup.sh && lunch <目标>
m liboperit_ripgrep    # 首验：rustlibs 解析 + edition 2021 编译
m Operit               # 全绿复验（jni_libs 切源码模块）
```

关注点：
- liboperit_* 七模块与树内 libjni/libserde/libserde_json/libmemchr 等解析
- Gradle CI 已绿（36119248931）：降级版 wrapper 编译 + .so 产出正常
- 若树内 android-crates-io 的 serde/json 版本req 满足不了 vendor 件（理论
  不会，globset 0.4.16 只 req log/bstr/regex 系），按报错单件补 vendoring


## 树侧首验战报（2026-09-26，cnb 云机 dodge，全绿）

m liboperit_ripgrep ✓（1m29s）、m Operit ✓。五道 Soong 关的修法（全部已入库）：

1. stem 命名律：库名必须 lib<crate_name> 开头，七件显式 stem（b02c6dc）
2. dylib 安装位冲突：模块类型改 rust_library_rlib 纯依赖形态（84e3ffd）
3. Soong 不展开 feature 蕴含：regex 两件按全叶闭包显式列 26 项（ef85940）
4. dylib 消费者的 rustlibs 解析到 dylib 变体致 clippy 加载失败：wrapper 改
   rlibs（树内 betosync 同款，1bec8e5）
5. -D missing-docs 门禁：wrapper 补文档注释（661b61c）

环境雷：树内 Operit clone 曾指 cnb.cool 第三方镜像（SekaiMoeAOSP）拉不到
新头，改指 GitHub 本尊。STT 模型资产树侧通路见
tools/intree/sync_stt_models.py（75505fd），APK 实证 213MB 含全部模型。
