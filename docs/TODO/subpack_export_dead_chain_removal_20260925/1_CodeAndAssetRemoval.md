# 1. Kotlin 与资产删除 [DONE]

## 旧实现情况

- core/subpack/：ApkEditor（fromAsset subpack/android.apk）、ApkReverseEngineer、
  ExeEditor/ExeIconChanger（改 assistance_subpack.exe 图标）、KeyStoreHelper
  （assets jks.jks/pkcs12.keystore 种子）
- ExportDialogs.kt 1083 行：ExportPlatformDialog/Android/Windows 三对话框、
  进度/完成对话框、exportAndroidApp/exportWindowsApp、图标裁剪与 zip/拷贝助手
- htmlpackager/：工具箱 HTML 打包器屏，唯一功能即调用两条死导出链

## 意图修正

功能必崩（资产缺失），整刀删除。

## 期待的新实现

无。不写回退分支。
