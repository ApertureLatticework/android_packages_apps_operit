# 依赖接入登记表（libs/）

本仓库经 repo sync 直接克隆为 `packages/apps/Operit`，包内全部第三方依赖在此登记。
模块命名一律 `operit-` 前缀，`visibility: ["//packages/apps/Operit:__subpackages__"]`。

## 策略（2026-09-20 修订定案）

| 类别 | 路径 | 说明 |
| --- | --- | --- |
| androidx / Kotlin 系 | 树内直引 | prebuilts/sdk/current/androidx 与 external/kotlin*，模块名按 pom2bp 约定 |
| 非 androidx Java/Kotlin | **包内 prebuilt** | 首批已入库：122 模块 / 125.2MB / sha256 lock 钉扎（最大单件 onnxruntime 24MB）；由 [tools/intree/import_prebuilts.py](../tools/intree/import_prebuilts.py) 生成每库 Android.bp/NOTICE。Gradle 与 Soong 消费同一份二进制，行为一致 |
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

## ffmpeg 源码线（fork 已落地，2026-09-22）

- manifest：`../local_manifests/upstream-ports.xml` 钉 Operit fork
  （ApertureLatticework/android_external_ffmpeg，承载分支 lineage-23.2）commit e6285e1；
  经由 dabao1955 个人 fork 中转后由用户手动接位组织仓（中转仓保留作重定向）
- fork 定制（四步 + v4l2request 摘除）：根 bp 两处 vendor 翻 false；补
  libavfilter/libavdevice Android.bp（root .c + aarch64，x86/.asm 不收）；
  libffmpeg_cli 模块（fftools 十四文件 + -Dmain=ffmpeg_cli_main）；
  config 已随仓 check-in；另摘 v4l2request 线（CONFIG_V4L2_REQUEST 与六 codec
  V4L2REQUEST_HWACCEL 归零 + bp 摘源摘 libudev）——Android 无 libudev，
  树内 libudev-zero 名不符且围栏隔离
- 链接性核查：上游无 -fvisibility=hidden（shared 全导出可直链 fftools），
  fftools 零 avpriv_ 实际调用
- 包内侧：`native/operit-ffmpeg/`（Android.bp.tree 暂存，链 libav* + libffmpeg_cli）；
  消费面实测仅 6 函数（execute×4 / getMediaInformation×3 / 版本串×2），
  门面 `app/src/soong/java/.../ffmpeg/FFmpegKit.kt` 与 ffmpeg-kit 同名同形，
  门面包名与 AAR 一致（com.arthenica.ffmpegkit），FFmpegUtil/StandardFFmpegTool/OpenSourceLicenses 的 import 双世界零改动
- Gradle 过渡期不动：Gradle 继续消费 ffmpeg-kit AAR，薄壳只进树内构建

## native 线（2026-09-22 定案：sherpa 走 prebuilt，其余源码收包）

| 模块 | Soong 形态 | 来源 | 状态 |
| --- | --- | --- | --- |
| libquickjsjni | cc_library_shared（quickjs/Android.bp 已落地） | bellard/quickjs master@04be246（VERSION 2026-06-04）收源入 quickjs/src/main/cpp/quickjs-upstream/；master 已删 libbf，5 个 .c 即全引擎 | ✅ 已接线 |
| libstreamnative | cc_library_shared（app/src/main/cpp/Android.bp 已落地） | 源一直在包内 | ✅ 已接线 |
| libtoolpkgwasm(+vm) | cc_library_shared/static（同上 bp + native/wasm-micro-runtime/Android.bp 已落地） | bytecodealliance/wamr main@b70d708（2.4.3）按 cmake 探针导出的精确文件清单收源（fast-interp、仅 libc-builtin、无 JIT/AOT/WASI），2.7MB | ✅ 已接线 |
| libsherpa-ncnn-jni | cc_prebuilt_library_shared（Android.bp 已由 CI 翻牌，arm64-v8a 单 ABI 对齐 abiFilters） | sherpa-ncnn+ncnn+openfst 三件套静态合体 .so（sha256 b5e93d3f…，5.0MB，commit 67231fe）；android-build 的 commit_native_prebuilts 开关自动抽取回写（tools/intree/extract_native_prebuilts.sh） | ✅ 已接线 |
| liboperit_ripgrep | cc_prebuilt_library_shared（app/src/main/jniLibs/Android.bp 已落地） | 源在库（tools/native_ripgrep），Cargo 依赖树内 crates 无对应故走 prebuilt（2026-09-22 定案）；.so 本位即 jniLibs（CI cargo 步骤产出），Soong 与 Gradle 消费同一文件 | ✅ 已接线 |

## 资产

- STT 模型群（sherpa-ncnn zipformer 双语 ~140MB、silero-vad onnx）：取消构建期下载，
  收源时经 `app/config/stt-model-assets.properties` 校验后直接落 `app/src/main/assets/models/`
- accessibility.apk 已在库随 assets 走；desktop.apk 与 showerclient 已随步骤 7 删除

## 树内专用文件

- `app/src/soong/java/`：Gradle 不编译、Soong 编译的源（BuildConfig 与 ffmpeg 门面）
- `Android.bp`：主模块树内定义（2026-09-22 由 .tree 翻牌，前置依赖全部树内验明）
- `native/operit-ffmpeg/Android.bp`：ffmpeg 薄壳（同批翻牌）

## 机器实证补遗（2026-09-22，m Operit 全绿战报）

- **KMP 空壳 jar 陷阱**：`kotlinx-serialization-json`（根坐标）在 Maven 是 KMP 根产物——
  jar 仅 64 条目、无 `JsonElement.class`。解析须直钉 `-jvm` 坐标（json/core 均然），
  且 `.module` 的 `available-at` 重定向变体 `files` 为空数组，须参与 publishable 候选
  （否则被"有 files"过滤误杀，回退选中根元数据壳）。`import_prebuilts.py` 已固化两处。
- **hnswlib 版本双轨**：`libs.versions.toml` 记 0.0.46（包名 `com.github.jelmerk.knn.*`），
  `app/build.gradle.kts` 内联钉 1.2.1（包名 `com.github.jelmerk.hnswlib.core.*`）——
  Gradle 以内联为准；本管线 ROOTS 已对齐 1.2.1。教训：目录扫不全，须再扫内联坐标。
- **kotlinx-coroutines-guava 1.10.2**（包内 prebuilt）：`ListenableFuture.await()` 扩展，
  树内 kotlinx.coroutines 无 guava 变体；传递闭包带入 guava 33.3.1-android。
  实编注记：树内 work-runtime-ktx 的 `Operation.await` 与其争位，WorkflowScheduler
  已改 IO 域阻塞 `.get()` 绕开解析歧义。
- **编译器插件二连**：`-Xplugin=` 序列化插件之外再挂 `parcelize-compiler.jar`
  （`kotlin-parcelize-runtime` 为树内模块，入 static_libs）。
