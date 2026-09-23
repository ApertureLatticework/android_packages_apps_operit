---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: fix/mcp-local-form-removal
Status: 计划定稿，动工中
---

# MCP 本地表单与本地安装链清理

## 原有状况

aosp_native_priv_integration 步骤 1 已删本地 MCP 运行时（MCPBridge/MCPDeployer/
MCPSharedSession/BridgeMcpRuntimeSession，MCPStarter 重写 remote-only），但安装侧
整条链还在，形成"装得下、跑不了"的死链路：

- `MCPConfigScreen`（1910 行）添加对话框三 tab：repo URL 导入 / ZIP 导入 / 远程连接；
  前两者触发本地插件安装流程
- `MCPViewModel.installServerWithObject/installServerFromZip` →
  `MCPRepository.installMCPServer*` → `installPluginInternal`：GitHub zip 下载、
  解压、`MCPProjectAnalyzer` 项目分析、`savePluginMetadata` 落本地插件目录
- `plugins/` 分析子系统三件（ProjectAnalyzer 518 行 / CommandGenerator 283 行 /
  ConfigGenerator 331 行）仅服务本地安装，产物无处消费
- `MCPLocalServer.PluginMetadata.type` 仍区分 "local"/"remote"，本地插件的
  安装态、启用态、配置管理面照常运转
- 卸载链 `uninstallMCPServer` 删本地目录与元数据

## 意图

remote-only 语义贯彻到底：本地安装的表单、流程、分析器、目录管理整刀删除，
插件面收敛为远程连接单一形态。

## 期待的新实现

- 添加对话框收敛为远程连接单 tab（endpoint/连接类型/bearer/headers 保留）
- PluginMetadata.type 字段删除，全库 remote 单态；历史存量 local 插件元数据
  迁移时静默丢弃（remote-only 形态下它们不可运行，保留只会渲染死卡片）
- 删除清单：
  - `data/mcp/plugins/MCPProjectAnalyzer.kt`、`MCPCommandGenerator.kt`、
    `MCPConfigGenerator.kt`、`ProjectStructure.kt`
  - `MCPRepository`：installMCPServer / installMCPServerWithObject /
    installMCPServerFromZip / uninstallMCPServer、installPluginInternal、
    installPluginFromZipInternal、downloadRepositoryZip、getGithubDefaultBranch、
    savePluginMetadata、pluginsBaseDir 常量族
  - `MCPViewModel`：installServerWithObject / installServerFromZip / uninstallServer
    local 分支 / setSelectedZipUri
  - `MCPConfigScreen`：repo/ZIP 两 tab 全 UI、awaitPluginVisible 安装等待、
    本地插件状态徽章与卸载入口
- `McpConfigImportParser`：mcp_config.json 导入保留，命令型条目（command/npx/uvx）
  解析后按"不支持"显式报错提示，不静默装死插件
- 八语字符串：本地安装相关键清除

## 作用域

- `ui/features/packages/`（ConfigScreen、ViewModel、MCPPackageDetailsDialog 等）
- `data/mcp/`（Repository、MCPLocalServer、plugins/）
- 市场入口：third_party_market 若仍下发 local 型插件卡片，安装按钮对 local 型
  禁用并提示 remote-only

## 非目标

- 不动远程 MCP 运行时（RemoteMcpRuntimeSession / MCPStarter remote 线）
- 不动 mcp_config.json 的远程条目导入

## 步骤

1. [本地安装链与表单删除](1_LocalInstallRemoval.md)
2. [元数据单态化与存量迁移](2_MetadataSingleState.md)

## 验证

- 全仓 rg：installPluginInternal / MCPProjectAnalyzer / PluginMetadata.type 无残留
- CI 编译绿；孤儿字符串检测器零错（八语同步）
- 手工面：添加对话框仅远程 tab；旧存量 local 插件升级后不再出现
