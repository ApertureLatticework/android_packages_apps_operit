# 1. SDK 对齐与签名参数上移

## 旧实现情况

`app/build.gradle.kts`：

- `defaultConfig`：`minSdk = 26`、`targetSdk = 34`
- APK 轮换签名任务：`--min-sdk-version 26`、`--rotation-min-sdk-version 28`

`Android.bp` 的 `android_app "Operit"` 为 `min_sdk_version: "31"`。Gradle 告出的 manifest
`uses-sdk` 因此与树内产物不一致：同一份源码，两条构建线声明不同的最低平台。

## 意图

Gradle 线对外声明与 Soong 一致，且不再为 API 26/27 保留任何验签分支。

## 期待的新实现

- `minSdk = 31`（对齐 `Android.bp`）
- `targetSdk = 36`（对齐 `compileSdk`）
- 签名参数：`--min-sdk-version 31`、`--rotation-min-sdk-version 31`，
  注释由「API 26/27 走 V2 老签名」改为「minSdk 31 > 28，平台一律走 V3 块」

## 作用域

- `app/build.gradle.kts`（默认配置区与 `rotateApkSigning` 任务）

## 兼容性

- `minSdk`/`targetSdk` 只影响 manifest `uses-sdk` 与 apksigner 的签名块选择；
  ROM 内安装路径固定 `system/priv-app`，不受 `system/` 分区对高 `targetSdk` 的安装限制
- `targetSdk = 36` 对齐 `compileSdk = 36`，消除「编译 API 高于目标 API」的组合；
  运行时目标面只对本应用生效，API 31-35 设备上系统按实际 `Build.VERSION.SDK_INT` 裁决

## 验证

- `grep -n "minSdk\|targetSdk" app/build.gradle.kts` 与 `Android.bp` 的 `min_sdk_version` 一致
- 打包出的 manifest `uses-sdk` 为 `minSdkVersion=31 targetSdkVersion=36`
