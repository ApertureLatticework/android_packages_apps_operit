---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: feat/native-rom-integration
Status: executing（第一梯队落地中）
---

# APK 体积精简

## 现状盘点（2026-09-19 实测）

- assets 共 32M，其中 desktop.apk 6.5M、templates/ 11M、accessibility.apk 2.7M、shizuku.apk 2.5M、emoji/ 3.7M、operit.png 1.4M、bridge/ 1.4M
- res 共 5.6M，九种语言（默认 zh + en/es/id/ja/ko/ms/pt-rBR/ro 各约 0.7M）
- release 与 nightly 构建均 isMinifyEnabled=false、isShrinkResources=false，全量类与资源直接入包
- ABI 已单裁 arm64-v8a，无优化空间

## 意图

ROM 独占分发，system 分区寸土寸金；安装包体积按梯队压缩，删除项与迁移步骤联动，不孤立行动。

## 梯队划分

第一梯队（本轮，无争议项）：

- release 与 nightly 开启 minify 与 shrinkResources，补齐 keep 规则
- 语言资源裁剪为默认中文与英文，八语全部出包
- 删除 assets/bridge/（terminal 线尸体，MCPStarter 已改 remote-only，零代码引用，f44c9d4 漏删）
- operit.png 1024×1024 RGBA 1.4M 重编码为 512×512 WebP，改引用名

第二梯队（步骤 4 Shizuku 线整删时一并）：

- shizuku.apk 2.5M 与 ShizukuInstaller

第三梯队（步骤 7 副屏后端原子切换时一并）：

- desktop.apk 6.5M 与 Shower 桌面客户端链

观察项（现役，不动）：

- templates/ 11M：AI 工作区与工具包开发的 Android 工程模板，WorkspaceUtils 现役消费
- accessibility.apk 2.7M：无障碍档现役，步骤 4 权限阶梯改造时评估保留形态
- emoji/ 3.7M：CustomEmojiRepository 运行时读取，现役

## 验证

- CI Android Build 编译通过（R8 规则缺失会在此暴露）
- workflow 产物体积对比：本轮 APK 与上轮差值应显著（语言资源约省 4M、bridge 1.4M、png 1.3M，minify 收益视类规模）
