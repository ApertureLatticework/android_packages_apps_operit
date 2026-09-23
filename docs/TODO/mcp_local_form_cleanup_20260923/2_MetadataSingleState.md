# 2. 元数据单态化与存量迁移

## 旧实现情况

- `PluginMetadata.type: String = "local"`（"local"/"remote" 双态），构造点散布
  Repository/ConfigScreen/市场导入
- 本地插件元数据持久化在 MCPLocalServer 的 mcp_servers.json，存量用户可能带着
  local 型条目升级

## 意图修正

remote 单态，字段消亡；存量 local 条目迁移时丢弃。

## 期待的新实现

- `PluginMetadata` 删 type 字段；所有构造点与判等点（type == "remote"）改单态直通
- `MCPLocalServer` 加载 mcp_servers.json 时过滤 type != "remote" 的存量条目并
  一次性重写存储文件（迁移即删除，无兼容分支）
- 配置状态面（serverStatus/isServerEnabled/getPluginConfig/savePluginConfig）
  语义不变，仅类型面瘦身
- `McpRuntimeDescriptor` 相关 local 枚举若步骤 1 有漏网一并清除

## 验证

- 构造存量 local 元数据 JSON 的升级用例：加载后列表仅剩 remote 条目，
  存储文件重写后无 local 痕迹
- rg "\\\"local\\\"" 于 data/mcp/ 与 ui/packages/ 零命中
