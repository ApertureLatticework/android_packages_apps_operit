# 依赖收源登记表（libs/）

本仓库经 repo sync 直接克隆为 `packages/apps/Operit`，因此包内全部第三方依赖源码收进本仓。本表是收源工单与依赖图审计基线，每条落地后在状态列打勾。

模块命名一律 `operit-` 前缀，`visibility: ["//packages/apps/Operit:__subpackages__"]`，各库自带 `Android.bp` 增量落地（独立可编，未接线前对全树零破坏）。主模块 `Android.bp.tree` 在全部模块齐备后翻牌。

## 树内直引（零收源）

androidx 与 Kotlin 走 `prebuilts/sdk/current/androidx/` 与 `external/kotlin*` 既有模块，pom2bp 命名约定 `group:artifact → group_artifact`：

| 模块名 | 对应坐标 | 核实状态 |
| --- | --- | --- |
| androidx.compose.runtime_runtime 等 Compose 全家 | compose-bom 2024.x | spike1 树内已核实 |
| androidx.appcompat_appcompat、com.google.android.material_material | appcompat / material | 1.txt pom2bp 清单已核实 |
| androidx.room_room-runtime / room-ktx | room 2.x | spike1 已核实（树内有 room-compiler-plugin） |
| androidx.datastore、work、window、webkit、glance、security-crypto、media3 | — | 待树内 `module-info.json` 核对 |
| kotlin-stdlib、kotlin-reflect、kotlinx-coroutines-android、kotlinx-serialization-json | — | 待树内核对 |

exoplayer 2.19.1 按[步骤 1 改判](../docs/TODO/aosp_native_priv_integration_20260919/1_ScopeCutAndDependencyManifest.md)迁 androidx.media3：收源期执行 `com.google.android.exoplayer2` → `androidx.media3` 包名替换，11 文件。

## Java/Kotlin 收源（libs/<name>/）

| 模块名 | 坐标 | 版本 | 支撑面 | 上游 | 状态 |
| --- | --- | --- | --- | --- | --- |
| operit-okio | com.squareup.okio:okio | 3.x | okhttp 伴生 | github.com/square/okio | 待收 |
| operit-okhttp | com.squareup.okhttp3:okhttp | 4.12.0 | 网络栈全量 | github.com/square/okhttp | 待收 |
| operit-okhttp-sse | okhttp3:okhttp-sse | 4.12.0 | SSE 流 | 同上 | 待收 |
| operit-okhttp-logging-interceptor | okhttp3:logging-interceptor | 4.12.0 | 调试日志 | 同上 | 待收 |
| operit-ktor-client-okhttp | io.ktor:ktor-client-okhttp | 3.2.3 | MCP SDK 传输 | github.com/ktorio/ktor | 待收 |
| operit-mcp-sdk-client | io.modelcontextprotocol:kotlin-sdk-client | 0.10.0 | 远程 MCP | github.com/modelcontextprotocol/kotlin-sdk | 待收 |
| operit-jsoup | org.jsoup:jsoup | 1.16.2 | HTML 解析 | jsoup.org | 待收 |
| operit-zxing-core | com.google.zxing:core | 3.5.3 | 二维码 | github.com/zxing/zxing | 待收 |
| operit-java-diff-utils | io.github.java-diff-utils | 4.12 | diff 工具 | github.com/java-diff-utils | 待收 |
| operit-apksig | com.android.tools.build:apksig | 8.1.0 | APK 签名/校验 | cs.android.com (platform tools base) | 待收 |
| operit-apk-parser | net.dongliu:apk-parser | 2.6.10 | APK manifest 解析 | github.com/hsiafan/apk-parser | 待收 |
| operit-axml | com.github.Sable:axml | 2.0.0 | 二进制 XML | github.com/Sable/axml | 待收 |
| operit-zipalign-java | com.github.iyxan23:zipalign-java | 1.2.1 | ZIP 对齐 | github.com/iyxan23/zipalign-java | 待收 |
| operit-commons-compress | org.apache.commons:commons-compress | 1.25.0 | 压缩格式 | commons.apache.org | 待收 |
| operit-commons-io | commons-io | 2.13.0 | IO 工具 | commons.apache.org | 待收 |
| operit-zip4j | net.lingala.zip4j:zip4j | 2.11.5 | ZIP 加解密 | github.com/srikanth-lingala/zip4j | 待收 |
| operit-androidsvg | com.caverock:androidsvg-aar | 1.4 | SVG 渲染 | github.com/badlogic/androidsvg | 待收 |
| operit-android-gif | com.github.penfeizhou.android-gif-drawable | — | GIF 解码 | github.com/penfeizhou/... | 待收 |
| operit-image-cropper | com.github.CanHub:Android-Image-Cropper | — | 背景裁剪 | github.com/CanHub/Android-Image-Cropper | 待收 |
| operit-jlatexmath | ru.noties:jlatexmath-android | 0.2.0 | LaTeX 渲染 | github.com/noties/markwon | 待收 |
| operit-renderx | — | 1.0.0 | LaTeX 渲染链 4 文件 | 依 libs.versions.toml 溯源 | 待收 |
| operit-coil 三条 | io.coil-kt | 2.5.0 | 图片加载 33 文件 | github.com/coil-kt/coil | 待收 |
| operit-itextg | com.itextpdf:itextg | 5.5.10 | PDF 导出 | github.com/LibrePDF/OpenPDF 系 | 待收 |
| operit-pdfbox-android | com.tom-roush:pdfbox-android | 2.0.27.0 | PDF 解析 | github.com/TomRoush/PdfBox-Android | 待收 |
| operit-junrar | com.github.junrar:junrar | 7.5.5 | RAR | github.com/junrar/junrar | 待收 |
| operit-poi 三条 | org.apache.poi | 5.2.3 | DOC/DOCX 工具与预览 | poi.apache.org | 待收 |
| operit-gson | com.google.code.gson | 2.10.1 | JSON | github.com/google/gson | 待收 |
| operit-hjson | org.hjson:hjson | 3.0.0 | 人读 JSON | hjson.org | 待收 |
| operit-uuid | com.benasher44:uuid | 0.8.2 | Kotlin UUID | github.com/benasher44/uuid | 待收 |
| operit-jieba | com.huaban:jieba-analysis | 1.0.2 | 中文分词 + 词典 | github.com/huaban/jieba-analysis | 待收 |
| operit-hnswlib 双条 | com.github.jelmerk | 0.0.46 | 向量近邻 | github.com/jelmerk/hnswlib-java | 待收 |
| operit-bcprov | org.bouncycastle:bcprov-jdk18on | 1.78 | 加密 | bouncycastle.org | 待收 |
| operit-nanohttpd | org.nanohttpd:nanohttpd | 2.3.1 | 本地 HTTP | nanohttpd.org | 待收 |
| operit-colorpicker / backdrop / liquid / reorderable / swipe | compose 主题件 | 各版本 | UI 组件 | 各上游 | 待收 |
| operit-accompanist-systemuicontroller | com.google.accompanist | 0.32.0 | 状态栏控制 | github.com/google/accompanist | 待收 |
| operit-smart-exception 双条 | com.arthenica | 0.2.1 | ffmpegkit 伴生 | github.com/tanersener/... | 待收 |

## 记忆/向量推理线（Java 壳 + native）

| 模块 | 坐标 | native 伴生 | 状态 |
| --- | --- | --- | --- |
| operit-tensorflow-lite | org.tensorflow:tensorflow-lite 2.10.0 | libtensorflowlite_jni | 待收（源码入 native/tflite） |
| operit-mediapipe-tasks-text | com.google.mediapipe:tasks-text 0.10.11 | libmediapipe_tasks_text_jni | 待收 |
| operit-onnxruntime | com.microsoft.onnxruntime 1.17.1 | libonnxruntime | 待收；当前仅 Silero VAD 单用途 |

## 唯一 prebuilt 例外

ML Kit text-recognition 五条 AAR 收 `libs/mlkit/` 以 `android_library_import` 引入（步骤 1 决策，全树唯一二进制例外）。

## native 收源（native/）

| 模块 | Soong 形态 | 来源 | 状态 |
| --- | --- | --- | --- |
| libquickjsjni | cc_library_shared | quickjs/src/main/cpp 已在库（C 源现为构建期 git fetch，收源入 native/quickjs，版号对齐 fetch tag） | 待收 |
| libstreamnative | cc_library_shared | app/src/main/cpp/streamnative 已在库 | 待接线 |
| libtoolpkgwasm + wamr | cc_library_static | wasm-micro-runtime（现构建期 fetch） | 待收 |
| libsherpa-ncnn-jni (+ncnn+openfst) | cc_library_shared | sherpa-ncnn（现构建期 fetch） | 待收 |
| libffmpegkit | cc_library_shared | ffmpeg-kit 自编 AAR 改源码直编，configure→Soong 转写为最大单项 | 待收 |
| liboperit_ripgrep | rust_ffi_shared | tools/native_ripgrep 已在库（rust 源） | 待接线 |

## 资产

- STT 模型群（sherpa-ncnn zipformer 双语 ~140MB、silero-vad onnx）：取消构建期下载，收源时由 `app/config/stt-model-assets.properties` 校验后直接落 `app/src/main/assets/models/`
- accessibility.apk 已在库随 assets 走；desktop.apk 与 showerclient 随步骤 7 删除

## 树内专用文件

- `app/src/soong/java/com/ai/assistance/operit/BuildConfig.java`：Soong 不生成 BuildConfig，翻牌时落此文件（VERSION_NAME/VERSION_CODE 随发版同步；Gradle 侧仍走 AGP 生成，两轨不同源）
- `Android.bp.tree`：主模块暂存名，翻牌规则见文件头
