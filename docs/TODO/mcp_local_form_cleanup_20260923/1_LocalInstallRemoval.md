# 1. 本地安装链与表单删除

## 旧实现情况

见 index.md"原有状况"清单：三 tab 对话框、ViewModel 三法、Repository 安装/下载/
解压/分析全链、plugins/ 三分析器。

## 意图修正

安装能力只留远程连接录入；本地安装代码删净，不留禁用态 UI。

## 期待的新实现

- `MCPConfigScreen`：
  - importDialog 收敛单 tab，isRepoImport/isZipImport 分支与对应输入态、
    ZIP 文件选择器（ACTION_OPEN_DOCUMENT 回调）删除
  - 本地插件卡片操作（安装进度条、卸载按钮、awaitPluginVisible 等待环）删除，
    卡片仅剩启用开关与详情（远程语义）
  - 338-346 行"本地插件跳过工具获取"补丁注释与分支随 type 字段消亡自然清理
- `MCPViewModel`：删除 installServer*（WithObject/FromZip/无参重载）、
  uninstallServer 的 local 分支、selectedZipUri 状态族、InstallProgress/
  InstallResult 中只服务本地安装的态（Preparing/Downloading/Analyzing 等，远程
  录入无进度概念）
- `MCPRepository`：删除 296-475 行安装四法与 installPluginInternal、
  installPluginFromZipInternal、downloadRepositoryZip、getGithubDefaultBranch、
  downloadFromUrl（若无他处引用）、savePluginMetadata；pluginsBaseDir 与
  OperitPaths 对应目录常量删；loadPluginsFromMCPLocalServer 收敛为远程加载
- `plugins/` 四文件整删（ProjectAnalyzer/CommandGenerator/ConfigGenerator/
  ProjectStructure）
- 市场面：MarketInstallMarker 与市场详情页对 local 型的安装入口禁用加提示，
  remote 型照常

## 验证

- CI 编译绿；添加对话框三 tab → 单 tab 截图对比
- 市场页 local 型卡片安装按钮禁用、提示文案八语在位
