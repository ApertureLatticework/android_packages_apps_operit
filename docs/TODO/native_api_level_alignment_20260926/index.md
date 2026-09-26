---
Repository: https://github.com/ApertureLatticework/android_packages_apps_Operit
Branch: lineage-23.2
Status: 已落地（2026-09-26；配套完善见文末验证）
---

# native 线 API 档对齐与一致性门禁

## 原有状况

同一 Android API 档在仓内四处独立字面声明：三份 workflow 的 `ANDROID_API_LEVEL`
env（android-build / android-tests / pr-check）+ `tools/native_ripgrep/build_native_ripgrep.ps1`
的 `[int]$ApiLevel` 默认值。981c0a9 之前为 CI `'26'`×3 + ps1 `23`——
linker driver 名即平台目标（`android26-clang` 把 minSdk=26 写进
`.note.android.ident` 并钉死 NDK API 26 符号面），与 AGP `minSdk=31` 已给
CMake 传 `ANDROID_PLATFORM=31` 的面不一致，且该错配靠人手扫出，无任何门禁兜住。

配套缺口：

- linker driver 缺失只在 cargo 步深处以晦涩报错间接暴露，restore/rustup 成本已付
- `build_native_ripgrep.ps1` 的 `$targetConfig` 挂着四 ABI 条目，而 jniLibs 与
  ffmpeg jni 均只发 arm64-v8a——三条目是能产出无 ffmpeg 伙伴库残废 jniLibs 的陷阱

## 意图

- 四处声明统一到 31，且「改一处漏三处」从此 CI 即红，不再依赖人手扫描
- linker driver 存在性预检 fail-fast：先于一切缓存恢复/rustup/cargo
- ps1 ABI 面与实际分发面（单 arm64-v8a）一致，意图显式注释化

取值本身（31/36）是产品决策（对齐 `Android.bp` `min_sdk_version: "31"` 与
`compileSdk = 36`，见 [ROM SDK 对齐](../rom_sdk_alignment_20260926/index.md)）；
本线只钉「同一档的多处声明一致」，不裁决取值。

## 期待的新实现

- 四处 `ANDROID_API_LEVEL` / `ApiLevel` 均为 31，缓存 key 含 api 变量，改值即换 key 重建一次
- 四处同构 cargo 步前置独立的「NDK linker driver 预检」步，同款注释显式声明
  「CI 只面向 arm64-v8a 单 ABI 为有意设计」
- `ci/script/check_android_api_level.py`：每处声明恰好一次（漏/重复都红）+ 四处同值，
  接线 android-build（漂移门禁后一步）
- `ci/test/test_android_api_level.py`：4 场景固化（一致绿 / 漏声明红 / 重复声明红 /
  取值不一致红）+ 真树不变量——断言脚本只在 push main / 手动触发时跑，PR 阶段由
  快速车道 unittest 钉住，漂移不再等合并后才红
- ps1 `$targetConfig` 收敛到单 aarch64 条目，「单 ABI 有意」注释下沉到 ps1 本体

## 非目标

- 不动 `compileSdk = 36` / `min_sdk_version`（产品决策面）
- 不为多 ABI 预留任何条件分支；扩 ABI 时按注释指引同步扩 ps1 表与 CI 预检清单

## 作用域

- `.github/workflows/android-build.yml`
- `.github/workflows/android-tests.yml`
- `.github/workflows/pr-check.yml`
- `tools/native_ripgrep/build_native_ripgrep.ps1`
- `ci/script/check_android_api_level.py`
- `ci/test/test_android_api_level.py`

## 步骤

1. [API 档面对齐 31](1_ApiLevelAlignment.md)
2. [linker driver fail-fast 预检](2_LinkerPrecheck.md)
3. [一致性门禁与配套完善](3_ConsistencyGate.md)
