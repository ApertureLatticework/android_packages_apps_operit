---
Repository: https://cnb.cool/SekaiMoeAOSPDev/android_packages_apps_Operit
Branch: feat/rom-sdk-alignment
Status: in_progress
---

# ROM 唯一形态的 SDK/资产/残留对齐

## 原有状况

本仓已按 [原生集成特权版计划](../aosp_native_priv_integration_20260919/index.md) 收敛为
「只随 LineageOS 23.2 分发」的单形态：ROOT/Shizuku 档删除、包内 prebuilt 化、Soong 主模块翻牌、
STT 模型改专仓 `external/operit-ttsmodels`、ripgrep 走树内源码线。但工具面仍留着双形态时代的尾巴：

- `app/build.gradle.kts`：`minSdk = 26` / `targetSdk = 34`，与 Soong 侧 `min_sdk_version: "31"` 不一致
- 签名轮换流程：`--min-sdk-version 26` / `--rotation-min-sdk-version 28`，为 API 26/27 继续验老签名而留
- `app/src/main/assets/README.md` + `shizuku_version.txt`：Shizuku 内置 APK 说明与版本号，Shizuku 线已整档删除
- `parser_implement_*` 等孤儿字符串仍留在九语 values 中

关键点：Gradle 侧 `minSdk`/`targetSdk` **不是**应用构建约束，编译期由 `compileSdk = 36` 决定；
`minSdk`/`targetSdk` 只进 manifest 的 `uses-sdk` 与 apksigner 的签名块选择。ROM 内安装路径固定为
`system/priv-app`，两者都不参与运行时权限裁决。

## 意图

让 Gradle 线对外表现为「ROM 独占单形态」：SDK 声明与 Soong 一致、资产与文档不再描述已删除能力。
本仓**不保留**上游普通发行形态（见原计划的非目标），因此不做任何条件分支或双形态兼容。

## 期待的新实现

- `app/build.gradle.kts`：`minSdk = 31`（对齐 `Android.bp` 的 `min_sdk_version: "31"`），
  `targetSdk = 36`（对齐 `compileSdk`）
- 签名轮换参数随 SDK 上移：`--min-sdk-version 31`、`--rotation-min-sdk-version 31`
  （V3 proof-of-rotation 自 API 28 起可选，31 > 28 无行为退化）
- 删除 `app/src/main/assets/README.md`、`app/src/main/assets/shizuku_version.txt`
- 资产目录新说明 `app/src/main/assets/ASSETS.md`：只描述现存资产（fonts/js/packages/templates/web/
  models 由 Gradle `syncSttModelAssets` 于构建期生成，Soong 侧由专仓 `asset_dirs` 提供）
- `values` 九语孤儿字符串随本轮清理

## 非目标

- 不动 `compileSdk = 36`：36 平台已编译通过是当前单一事实源，降回 34 会连带降 androidx/compose 版本
- 不动 Soong 侧 `min_sdk_version`（已是 31）
- 不动 Gradle↔Soong 双轨共存形态本身（原计划明确要求 Gradle 线全程可用）
- 不做任何「旧版本回退/降级」兼容逻辑

## 作用域

- `app/build.gradle.kts`
- `app/src/main/assets/`
- `app/src/main/res/values*/strings.xml`

## 风险

- Gradle CI lane 只跑 `assembleDebug` 与 JVM 单测，不会验证 `targetSdk = 36` 的真机行为；
  真机验证（安装、权限、前台服务）在用户树侧完成

## 步骤

1. [SDK 对齐与签名参数上移](1_SdkAlignment.md)
2. [Shizuku 残留与资产目录说明](2_AssetResidue.md)
