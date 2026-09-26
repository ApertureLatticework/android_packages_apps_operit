# 2. Shizuku 残留与资产目录说明

## 旧实现情况

- `app/src/main/assets/README.md`：Shizuku 内置 APK 的添加与更新说明
- `app/src/main/assets/shizuku_version.txt`：内容 `13.6.0`
- `values-es/id/ko/ms/pt-rBR/ro` 六语各 50 条 `shizuku_*` 字符串（默认与 en/ja 已清）

Shizuku 线在步骤 4 已整档删除（执行器、授权器、安装器、向导卡片、manifest 权限），
`shizuku.apk` 也已移除，只有上述资产与译文留在树上。

## 意图

工具面与资产面不再描述已删除的借权通道；assets 目录另立说明，讲清两条构建线如何取得 STT 模型。

## 期待的新实现

- 删除 `assets/README.md` 与 `assets/shizuku_version.txt`
- 新增 `assets/ASSETS.md`：逐目录说明现存资产，并写明 `models/` 为构建期生成：
  Gradle 走 `syncSttModelAssets`（清单 + SHA-256），Soong 走专仓
  `external/operit-ttsmodels/models` 的 `asset_dirs`
- 六语 `shizuku_*` 字符串删除

## 作用域

- `app/src/main/assets/`
- `app/src/main/res/values-{es,id,ko,ms,pt-rBR,ro}/strings.xml`

## 验证

- `rg -n "shizuku" app/src/main/res app/src/main/assets` 零命中
- `check_localizations.py` 零错（不新增 locale-config 违规，不动 `locales_config.xml`）
