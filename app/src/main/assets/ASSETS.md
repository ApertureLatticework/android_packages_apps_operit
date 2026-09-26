# assets 目录说明

本目录只包含随 ROM 分发的只读资产。Gradle 与 Soong 两条构建线消费同一份文件。

## 目录

- `fonts/`：界面与文档渲染所需字体
- `js/`：内置 JavaScript 与 shell 脚本
- `packages/`：随包分发的 ToolPkg 包
- `templates/`：工作流与包模板
- `web/`：内置 Web 资源
- `emoji/`、`model_logos/`：界面图像资源
- `test/`：随包测试样例
- `operit_shell_exec`、`logo.svg`、`operit_avatar.webp`：应用内引用的单文件资产

## models（构建期生成，不入库）

`models/` 是 STT/VAD 模型目录，由构建期准备，仓库内只有占位 `.keep`（见 `.gitignore`）：

- Gradle：`syncSttModelAssets` 任务按 `app/config/stt-model-assets.properties` 的清单与 SHA-256
  下载校验后落到 `build/generated/stt-model-assets`，再由 `syncMainAssets` 合入主 assets
- Soong：`Android.bp` 的 `asset_dirs` 直接指向专仓
  `external/operit-ttsmodels/models`（`local_manifests` 挂载，与主仓成对安装）
