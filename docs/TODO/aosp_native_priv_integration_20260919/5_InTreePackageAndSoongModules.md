# 5. 包内 Soong 模块化与 Android.bp

## 旧实现情况

构建完全由 Gradle 承担：Maven 拉取全部 Java 依赖，CMake 经 externalNativeBuild 编 quickjs、mnn、llama、terminal、streamnative，ffmpeg 与 ripgrep 由外部脚本预编后以 AAR 和 jniLibs 形态消费，STT 模型构建期联网下载，APK 独立签名并带密钥轮换流程。

## 意图

全部依赖源码收入 `packages/apps/Operit` 包内，以 Soong 模块自包含构建；除 ML Kit 外零 prebuilt。

## 期待的新实现

包结构：

```
packages/apps/Operit/
├── Android.bp                 # android_app "Operit"
├── AndroidManifest.xml
├── src/                       # app 源码与 check-in 的 Room 生成类
├── libs/                      # Java 与 AAR 依赖，模块名 operit- 前缀
│   ├── mlkit/                 # 唯一 android_library_import 例外
│   └── <各开源库>/
├── native/                    # cc_* 与 rust 模块
│   ├── quickjs/  mnn/  llama/  streamnative/
│   └── ripgrep/               # rust_ffi_shared
├── assets/models/             # STT 与向量模型，进树数据资产，无 rootfs
└── etc/privapp-permissions-operit.xml
```

要点：

- `android_app` 配置：`privileged: true`、`certificate: "platform"`、`compose: true`、`min_sdk_version: "26"`，static_libs 引用包内全部依赖模块
- 模块命名 `operit-` 前缀避开树内同名（AOSP 自带 external/okhttp 等），并加 `visibility: ["//packages/apps/Operit:__subpackages__"]`
- ffmpeg、onnxruntime、filament 及各 Java 库按步骤 1 白名单以源码入包，版本对齐当前 Gradle 所用 tag
- 每库一份来源登记：上游仓库、版本、裁剪说明与许可证文本
- STT 模型取消构建期下载，作为进树资产直接放置
- 废弃项：`.debug` 与 `.clone` 变体、APK 轮换签名流程、jniLibs 预置目录

## 作用域

- ~~LOS fork 侧新增 `packages/apps/Operit` 仓库，Operit 主仓提供源码同步与依赖裁剪~~（已修正，见下）
- 每个依赖库的 CMake 或 Maven 源码树到 Soong 模块的转写

## 验证

- 树内 `m Operit` 出包，安装运行等价于 Gradle 版功能面
- 依赖图审计：构建图中除 mlkit 外无任何 import 模块

## 风险

- Compose 编译器与树内 Kotlin 版本兼容矩阵是首要技术验证项，落在步骤 6 的 spike
- mnn 与 llama.cpp 的 Soong 转写工作量最大，在步骤 1 依赖清单定稿后立即启动；若排期过长则裁剪功能面减小模块面，不引入任何 prebuilt 过渡产物

## 树内 Compose 用法定稿（2026-09-19，spike1 翻车后实锤）

- `compose: true` 属性不存在于 LOS 23.2 Soong，写法为：Compose 模块直接进 static_libs
- Compose 编译器由 kotlinc_incremental 内嵌（kotlin-compose-compiler-embeddable 2.2.0），
  全树 Kotlin 模块默认可用；`-P plugin:androidx.compose.compiler.plugins.kotlin:...`
  为可选插件参数（官方 SceneTransitionLayoutDemo 同款）
- androidx 预编译落位 prebuilts/sdk/current/androidx/，非 prebuilts/androidx
- Operit 的 Soong 模块按 SceneTransitionLayoutDemo 范本组织（android_library 承载 Kotlin/Compose 源，
  android_app 壳引库 + platform_apis/platform_app_defaults）

## AGP→Soong 迁移机械动作：manifest package 属性（2026-09-19 踩坑）

Soong 的 manifest 处理器要求 `manifest:package` 显式声明；AGP 8 项目（Operit 现状）
包名在 build.gradle.kts 的 namespace（com.ai.assistance.operit），manifest 里没有该属性。
进树时迁移脚本需向 AndroidManifest.xml 注入 `package="com.ai.assistance.operit"`。

## 同步机制定案（2026-09-20，用户拍板）

本仓 origin 即 `android_packages_app_operit`，LOS repo manifest 直接挂本仓 URL，
`repo sync` 克隆本仓即成 `packages/apps/Operit` 包：

- 无独立树仓、无导出脚本；Soong 文件（Android.bp 系）与本仓 Gradle 结构共存，
  Gradle 构建全程可用（Trebuchet 双轨同款），Soong 直接引用 `app/src/main/...` 路径
- 未被 git 跟踪的 ref/、预编产物不进树；docs/、ci/、tools/ 对 Soong 惰性无害
- 依赖收源全部落在包内 libs/ 与 native/，工单见 [libs/DEPS.md](../../../libs/DEPS.md)

## 执行情况（2026-09-20 骨架落地）

- `app/src/main/AndroidManifest.xml` 注入 package 属性（与 namespace 同值，AGP 同值仅警告）
- `etc/privapp-permissions-operit.xml` 特权白名单建位（权限面为步骤 4/6/7 定案八条）
- `Android.bp.tree` 主模块暂存定义：android_library operit-lib + android_app Operit 壳
  （privileged/certificate platform/platform_apis/optimize shrink），翻牌规则见文件头——
  依赖模块先增量落地，主模块最后翻牌，过渡期树内 m 全量构建零破坏
- `libs/DEPS.md` 收源工单：树内直引模块名、Java/Kotlin 收源坐标、native 收源、
  ML Kit 唯一 prebuilt 例外、STT 模型资产化、树内 BuildConfig
- exoplayer→androidx.media3 迁移、STT 模型入 assets、约 40 坐标收源为后续增量批次，
  推进节奏待用户定案（收源量数据已呈交）

## 执行情况补充（2026-09-22，翻牌机械活清零）

- prebuilt 首批入库：122 模块 / 125.2MB / sha256 lock（tools/intree/import_prebuilts.py，
  Gradle Module Metadata 变体语义闭包解析）
- native 线定案落地：quickjs（收源 04be246）/ wamr（探针清单收源 b70d708）/
  streamnative 三模块真 bp 就位；sherpa 与 ripgrep 走 prebuilt（android-build 的
  commit_native_prebuilts 开关抽取回写 + 自动翻牌），ABI 面对齐 abiFilters 仅 arm64-v8a
- exoplayer → media3 1.8.0 迁移完成（9 Kotlin + 1 XML + Gradle）
- BuildConfig 树内版落地（app/src/soong/java，版本 bump 三处同步）+ manifest 版本属性补齐
- ffmpeg 门面改同包（com.arthenica.ffmpegkit）：消费方 import 双世界零改动
- ffmpeg fork 落地：dabao1955/android_external_ffmpeg@e6285e1（vendor 翻转 +
  libavfilter/libavdevice bp + libffmpeg_cli 四步全兑现，local_manifests 已切）
- 主模块翻牌条件收敛为：CI 落 sherpa .so + 用户树侧集成（repo sync + device.mk）

## 依赖策略修订（2026-09-20，用户拍板）

推翻“ML Kit 唯一 prebuilt 例外、其余零二进制”的原决策：

- 非 androidx 依赖一律包内 prebuilt（AAR/jar import，钉 sha256），接线器
  `tools/intree/import_prebuilts.py`（Gradle .module 变体语义闭包 + 三仓路由 +
  Android.bp/NOTICE/lock 生成）；树内直引（okhttp/gson 等 12 项已按 LOS manifest
  实证）降级为日后优化项
- ffmpeg 为唯一源码线例外：external/ffmpeg（dvab-sarma fork）+ 包内薄壳
  liboperit_ffmpeg + 树内同名门面，ffmpeg-kit AAR 与 smart-exception 随之退役
  （消费面实测仅 6 函数）；fork 清单在 libs/DEPS.md 与 local_manifests 注释
- 第三方 external/ 移植仓探索记录：onnxruntime 的 Jibar-OS 仓（prebuilt 包裹）
  搁置——包内 prebuilt 已含 onnxruntime-android AAR，无需外部仓
