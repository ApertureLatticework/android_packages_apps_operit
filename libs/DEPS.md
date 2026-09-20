# 依赖接入登记表（libs/）

本仓库经 repo sync 直接克隆为 `packages/apps/Operit`，包内全部第三方依赖在此登记。
模块命名一律 `operit-` 前缀，`visibility: ["//packages/apps/Operit:__subpackages__"]`。

## 策略（2026-09-20 修订定案）

| 类别 | 路径 | 说明 |
| --- | --- | --- |
| androidx / Kotlin 系 | 树内直引 | prebuilts/sdk/current/androidx 与 external/kotlin*，模块名按 pom2bp 约定 |
| 非 androidx Java/Kotlin | **包内 prebuilt** | AAR/jar 原样入库，`android_library_import`/`java_import` 接线；由 [tools/intree/import_prebuilts.py](../tools/intree/import_prebuilts.py) 生成每库 Android.bp/NOTICE 与 `prebuilts.lock`（sha256 钉扎）。Gradle 与 Soong 消费同一份二进制，行为一致 |
| ffmpeg | **树内源码线** | `external/ffmpeg`（dvab-sarma fork，ffmpeg 8.0 全源码 Soong 化）+ 包内薄壳 `liboperit_ffmpeg`（JNI）+ 树内同名门面（app/src/soong/java/.../ffmpeg/）。ffmpeg-kit AAR 与 smart-exception 依赖在树内不复存在 |
| native（sherpa-ncnn/wamr/quickjs/ripgrep/streamnative） | 收源进包 | 包内 cc/rust 模块（quickjs/streamnative 源已在库；sherpa/wamr/ripgrep 为构建期 git fetch，收源时入包） |

修订史：原决策“ML Kit 唯一 prebuilt 例外、其余零二进制源码收齐”作废——收源量（ffmpeg 400 万行、poi 250 万行起）与收益不匹配，且 AOSP/LOS 树自身即以 prebuilts/ 运行。

## prebuilt 接线（tools/intree/import_prebuilts.py）

- 根坐标 51 条与 app/build.gradle.kts 对齐；传递闭包按 Gradle Module Metadata（.module）
  变体语义解析（KMP 库走 androidTarget/androidJvm 变体，AGP 多 buildType 只取 release），
  树内已有供应的组（androidx/kotlin/coroutines/serialization）不进闭包
- 三仓路由：maven central / google maven / jitpack
- 产物：`libs/<module>/{工件, Android.bp, NOTICE}` + `libs/prebuilts.lock`
- 用法：`--dry-run` 看闭包 / `--fetch` 落盘 / `--verify` 校验 sha256
- 升级流程：改脚本根坐标版本 → 重跑 → 人工过 lock diff → 提交

### 树内直引核对记录（LOS 23.2 default.xml 实证）

以下坐标树内已有源码仓，**作为日后优化项**（想蹭 ROM 全局升级时逐个换，非当前路径）：
okhttp/okio（external/okhttp、external/okio）、gson、bouncycastle、jsoup、apksig
（tools/apksig）、zxing、nanohttpd、commons-compress/io、accompanist、tensorflow
（external/tensorflow，tflite 可用性待树内核）。frameworks/opt/colorpicker 为平台组件，
与本项目 Compose colorpicker 非同一物。

## ffmpeg 源码线

- manifest：`../local_manifests/upstream-ports.xml` 钉 dvab-sarma `android-16.0_r3-8.0`
  分支 commit 5dbfbaa
- fork 清单（建议 fork 至 ApertureLatticework 后切 manifest）：
    1. 根 Android.bp `ffmpeg_defaults`：`vendor: true → false`（system priv-app 不可链 vendor 库）
    2. 补 `libavfilter/Android.bp` 与 `libavdevice/Android.bp`（仿 libavcodec，源在树；现状 404）
    3. 新增 `libffmpeg_cli` 模块：fftools 全套 .c 以 `-Dmain=ffmpeg_cli_main` 编成库
    4. 核对 configure 产物（config.h 等）已随仓 check-in
- 包内侧：`native/operit-ffmpeg/`（Android.bp.tree 暂存，链 libav* + libffmpeg_cli）；
  消费面实测仅 6 函数（execute×4 / getMediaInformation×3 / 版本串×2），
  门面 `app/src/soong/java/.../ffmpeg/FFmpegKit.kt` 与 ffmpeg-kit 同名同形，
  翻牌时 FFmpegUtil/StandardFFmpegTool/OpenSourceLicenses 仅改 import 行
- Gradle 过渡期不动：Gradle 继续消费 ffmpeg-kit AAR，薄壳只进树内构建

## native 收源（收源时落包内 Android.bp）

| 模块 | Soong 形态 | 来源 | 状态 |
| --- | --- | --- | --- |
| libquickjsjni | cc_library_shared | quickjs/src/main/cpp 在库（C 源现为构建期 git fetch，收源入 native/quickjs） | 待收 |
| libstreamnative | cc_library_shared | app/src/main/cpp/streamnative 在库 | 待接线 |
| libtoolpkgwasm + wamr | cc_library_static | wasm-micro-runtime（构建期 fetch） | 待收 |
| libsherpa-ncnn-jni (+ncnn+openfst) | cc_library_shared | sherpa-ncnn（构建期 fetch） | 待收 |
| liboperit_ripgrep | rust_ffi_shared | tools/native_ripgrep 在库（rust 源） | 待接线 |

## 资产

- STT 模型群（sherpa-ncnn zipformer 双语 ~140MB、silero-vad onnx）：取消构建期下载，
  收源时经 `app/config/stt-model-assets.properties` 校验后直接落 `app/src/main/assets/models/`
- accessibility.apk 已在库随 assets 走；desktop.apk 与 showerclient 已随步骤 7 删除

## 树内专用文件

- `app/src/soong/java/`：Gradle 不编译、Soong 编译的源（BuildConfig 与 ffmpeg 门面）
- `Android.bp.tree`：主模块暂存名，翻牌规则见文件头
- `native/operit-ffmpeg/Android.bp.tree`：薄壳模块暂存名（随 ffmpeg fork 落地翻牌）
