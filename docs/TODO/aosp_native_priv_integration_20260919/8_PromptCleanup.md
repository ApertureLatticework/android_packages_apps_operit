# 8. 提示词精简

## 旧实现情况

提示词源集中在 core/config 四个文件，含大量 Linux 环境与终端会话描述：

- `SystemToolPrompts.kt`（1007 行）：shell 工具 environment 参数的 "linux" 分支文案，中英双份
- `SystemPromptConfig.kt`（712 行）：工作区挂载指引引导模型在 Linux 终端环境直接执行文件，中英双份
- `SystemToolPromptsInternal.kt`：终端会话工具族——create_terminal_session、execute_in_terminal_session、execute_hidden_terminal_command、input_in_terminal_session
- `FunctionalPrompts.kt`：功能级提示待同步排查

## 意图

功能裁剪与权限阶梯改造全部完成后，提示词按最终工具清单收敛：删除 Linux 环境分支、终端会话工具族、挂载执行指引，并对整体 token 面做一轮精简。

放最后的原因：提示词必须与真实存在的工具一一对应。先完成步骤 1 至 7 的代码定形，再收敛提示词，避免中途反复修订。

## 期待的新实现

- 提示词声明的能力与代码工具面一一对应，不描述已删除的工具
- 四个提示词源文件过一遍：删 linux/ubuntu/proot/terminal-session 相关段落
- 挂载指引改写为纯 Android 沙箱语义，不再提及 Linux 终端执行
- 精简前后 token 计数对比记录到本文件
- SkillManager 与 ToolPkg 文档面同步排查内嵌提示
- values 及各 locale 的孤儿字符串批量清理：avatar 配置、pet、mood、nav_assistant_config 等随功能删除而失去引用的条目
- autoglm 模型名警告文案（FunctionalConfigScreen、ClassicChatSettingsBar 的 showAutoGlmError）随步骤 7 对 autoglm 的去留定案后同步处理

## 作用域

- `core/config/SystemToolPrompts.kt`
- `core/config/SystemToolPromptsInternal.kt`
- `core/config/SystemPromptConfig.kt`
- `core/config/FunctionalPrompts.kt`
- Skill 与 ToolPkg 携带的提示文本

## 验证

- `rg -in "linux|ubuntu|proot|terminal_session" app/src/main/java/com/ai/assistance/operit/core/config/` 零残留（已达成，2026-09-20）
- 工具 schema 与工具实现类逐一对照，无孤儿声明（已达成：155 声明 vs ToolRegistration 零孤儿）
- token 计数对比入档（见下）

## 执行情况（2026-09-20）

- SystemToolPrompts/SystemToolPromptsInternal：environment 参数收敛为 `"android" | "repo:<仓库名>"`，copy_file 跨环境描述改仓库语义（中英双语，Internal 内 4+4 处）
- SystemPromptConfig：工作区指引删除 Linux 终端挂载说明与“直接执行”两条（中英），改纯 Android 沙箱语义
- PathMapper 整删：linux 分支不可达，resolvePath 恒等，五个调用点直通原路径
- 孤儿字符串：检测器全量扫描（java R.string + res/manifest @string 双向引用），按本 TODO 已裁功能簇（终端/termux、Shizuku/ROOT/ADMIN 档、avatar/pet/mood、部署桥）清除 343 键，八语共 2739 条目；检Localization 门禁零错
- token 对比：四文件 482427 → 481733 字符（Δ-694，粗估 token ~131380 → ~131171）；大额削减已在步骤 1 完成五个终端工具族与 schema 的删除，本轮为语义收敛尾刀
- autoglm 保留定案（步骤 7）：警告文案三处引用均在，不动

## 遗留（非本轮面）

- MCPConfigScreen 本地插件表单与 MCPLocalServer 插件管理面（步骤 1 遗留登记项，涉及 3000 行子系统，待专项）
- 历史性孤儿字符串约 1800 键（非本 TODO 功能裁剪所致），属仓库级 i18n 债务，另行专项
