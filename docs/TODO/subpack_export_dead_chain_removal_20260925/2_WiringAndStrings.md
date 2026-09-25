# 2. 路由接线与八语清除 [DONE]

## 旧实现情况

- OperitScreens：HtmlPackager 路由 object 与 import
- ScreenRouteRegistry：toolbox.html_packager hostEntryDefinition（order 160）
- AIChatScreen：五个导出状态位、onExportClick 触发块、五组对话框
- ChatScreenContent：同套状态位与对话框（触发线已死）
- 字符串：export_* 家族、screen_title_html_packager、tool_html_packager(_desc)

## 意图修正

入口全下线，孤儿键八语清除。

## 期待的新实现

聊天与工具箱菜单不再提供导出成 APP；无孤儿键。
