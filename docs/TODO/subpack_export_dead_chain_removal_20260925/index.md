---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: lineage-23.2
Status: 已落地（2026-09-25 执行完毕，见执行情况）
---

# subpack 导出死链下线（导出成 APK/EXE）

## 原有状况

「把 AI 工作区/HTML 打包成 Android APK 或 Windows EXE」功能：`core/subpack/`
五件（ApkEditor/ApkReverseEngineer/ExeEditor/ExeIconChanger/KeyStoreHelper）+
`ExportDialogs` 全家族 + 三个入口面（AIChatScreen、ChatScreenContent、
工具箱 HtmlPackager）。资产 `assets/subpack/android.apk`、`subpack/windows.zip`
已在早期裁撤中删除（现仅剩 `.keep` 墓碑），`exportAndroidApp`/`exportWindowsApp`
运行时开不存在的资产必 FileNotFoundException——装得下跑不了的死链，与
MCP 本地表单同款。KeyStoreHelper 以 assets 根的 `jks.jks`/`pkcs12.keystore`
为种子，同链陪葬。ChatScreenContent 的导出状态位从未被置 true，触发线先死。

## 意图

死链整刀删除，不留桩、不做兼容分支（功能已必崩，无可保留 userspace）。
templates/ 11M 为 AI 工作区现役脚手架，不在本刀范围。

## 期待的新实现

无导出成 APP 形态。聊天界面与工具箱不再出现导出入口。

## 作用域

- `core/subpack/` 整删；`ExportDialogs.kt` 整删；`htmlpackager/` 整删
- 接线：OperitScreens 路由、ScreenRouteRegistry 工具箱项、
  AIChatScreen/ChatScreenContent 的状态位与对话框块
- assets：`subpack/.keep`、`jks.jks`、`pkcs12.keystore`
- 八语：export_* 孤儿键、screen_title_html_packager、tool_html_packager(_desc)

## 非目标

- 不动 templates/（现役）
- 不动裁图库依赖（主题页等仍用）

## 步骤

1. [Kotlin 与资产删除](1_CodeAndAssetRemoval.md) [DONE]
2. [路由接线与八语清除](2_WiringAndStrings.md) [DONE]

## 验证

- 全仓 rg：subpack/ExportDialogs/HtmlPackager/exportAndroidApp 零残留
- 检Localization 门禁零错；CI 双 workflow 绿

## 执行情况（2026-09-25，分支 lineage-23.2）

- 删除：core/subpack/ 五件、ExportDialogs.kt（1083 行）、htmlpackager/ 工具箱屏、
  assets/subpack/.keep、assets/jks.jks、assets/pkcs12.keystore
- 接线：OperitScreens 路由与 import、ScreenRouteRegistry 工具箱项、
  AIChatScreen 状态位/触发块/五组对话框/孤儿 import（AITool 族）、
  ChatScreenContent 状态位与对话框块（该面触发线本就已死）、
  WorkspaceScreen 与 WorkspaceManager 的 onExportClick 穿参与 FAB 导出项
- 配置面：WorkspaceConfig 删 ExportConfig 与 export 字段，
  WorkspaceUtils 十份默认 config.json 模板去 export 节
- 字符串：28 孤儿键（export_* 25 + html_packager 族 3）八语清除，
  ExeEditor 空注释段三语清理
