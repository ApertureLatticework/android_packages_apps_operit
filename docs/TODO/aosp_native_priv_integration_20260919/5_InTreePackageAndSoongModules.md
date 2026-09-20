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
