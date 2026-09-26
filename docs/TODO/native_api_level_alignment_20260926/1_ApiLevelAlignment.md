# 1. API 档面对齐 31 [DONE]

## 旧实现情况

- 三份 workflow env：`ANDROID_API_LEVEL: '26'`
- `build_native_ripgrep.ps1`：`[int]$ApiLevel = 23`

CI cargo 线与本地 ps1 线各停各的旧档，且互不相同。

## 意图修正

linker driver 名即平台目标：NDK r25 unified toolchain 下 `aarch64-linux-android31-clang`
把 API 31 写进 `.note.android.ident` 并钉死该档符号面。AGP `minSdk=31` 已给 CMake 传
`ANDROID_PLATFORM=31`，cargo 线没理由停在 26；ps1 与 CI 产物平台面也不该不一致。

## 新实现情况（981c0a9）

- 三份 workflow：`ANDROID_API_LEVEL: '26'` → `'31'`
- ps1：`[int]$ApiLevel = 23` → `31`

缓存 key 均含 api 变量，改值自动换 key 重建一次，无手删缓存负担。

## 验证

android-build 的 cargo 步即 NDK 25.1.8937393 有无 `aarch64-linux-android31-clang`
的实测（r25 unified 覆盖 21-33，预期在）。
