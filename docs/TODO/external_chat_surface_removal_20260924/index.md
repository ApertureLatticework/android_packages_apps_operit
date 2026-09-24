---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: lineage-23.2（用户指示直接当前分支提交）
Status: 已落地（2026-09-24 执行完毕，见文末执行情况）
---

# 外部对话能力整体下线（HTTP/WebChat/A2A/Intent 三入口）

## 原有状况

外部对话共三入口、一套 executor，均随包发布形态存在：

- EXTERNAL_CHAT Intent 广播：`ExternalChatReceiver`（manifest exported receiver），
  本机任意 app 广播调起，结果回传广播
- 外部 HTTP 服务（默认 8094，Bearer 鉴权）：`ExternalChatHttpServer` 承载
  REST `/api/*`、A2A 1.0（Agent Card + JSON-RPC）、Web Chat 静态门面
- web-chat/：React 19 + Vite 前端工程，`npm run build:webchat` 构建后同步
  `app/src/main/assets/web-chat/`（gitignore 产物）；Soong 线无 npm 钩子，
  树内 `m Operit` 产物缺该资产（服务起得来、页面 404）

接线点：AIForegroundService（服务生命周期 + keepAlive 判定）、
EnhancedAIService 与 OperitApplication（启动门槛）、ActivityLifecycleManager
（AutoStarter）、OperitScreens 路由与 SettingsScreen 入口、
ExternalHttpApiPreferences、八语字符串 50 键 × 8 locale。

## 意图

按用户决策收敛为「本地 chat 单形态」：外部对话能力三入口整刀删除。
用户确认该能力未随正式版本发布（a2a_server TODO 中"已对外发布并由第三方
使用"的记录不实），按未发布处理，彻底清理，不做兼容分支，不留回退。

## 期待的新实现

无外部对话能力。app 对话仅本机 UI 一条路；AIForegroundService 的常驻判定
只看 alwaysListening 与后台保活，外部 HTTP 因子删除。

## 作用域

- `integrations/http/`、`integrations/a2a/`、`integrations/intent/ExternalChatReceiver.kt`、
  `integrations/externalchat/` 整删
- `data/preferences/ExternalHttpApiPreferences.kt`、
  `ui/features/settings/screens/ExternalHttpChatSettingsScreen.kt` 删除
- 接线点：AIForegroundService、EnhancedAIService、OperitApplication、
  ActivityLifecycleManager、OperitScreens、SettingsScreen、AndroidManifest
- `web-chat/` 工程、根 package.json `build:webchat`、.gitignore 两条目
- CI：android-build.yml、pr-check.yml、android-tests.yml 的 web 门控与步骤
- 文档：external_http_chat.md、external_a2a_server.md、external_intent_chat.md
  删除；BUILDING.md、CONTRIBUTING.md、ci/README.md 引用同步
- 八语字符串 50 键清除

## 非目标

- 不动本机 chat UI 与 AIForegroundService 其余职责
- 不动 workflow_intent_trigger 等其他 Intent 线
- 不动 tools/mcp_bridge（pnpm 工作区另一成员，与本线无关）

## 步骤

1. [Kotlin 线删除](1_KotlinLineRemoval.md) [DONE]
2. [前端工程与 CI 清理](2_FrontendAndCiRemoval.md) [DONE]
3. [文档与八语字符串清除](3_DocsAndStringsRemoval.md) [DONE]

## 验证

- 全仓 rg：ExternalChat / externalHttp / WebChatHttpBridge / a2a /
  EXTERNAL_CHAT / web-chat 零残留（docs/TODO 存档除外）
- 检Localization 孤儿字符串门禁零错
- Android.bp 无需改动（本就不接线 web-chat；删除物不在 srcs 清单内）

## 执行情况（2026-09-24，分支 lineage-23.2）

- Kotlin：integrations/http（7 文件含 bridge/ 四件）、integrations/a2a（2 件）、
  integrations/externalchat（3 件）、ExternalChatReceiver、ExternalHttpApiPreferences、
  ExternalHttpChatSettingsScreen 全删；manifest receiver 块删除；
  AIForegroundService 拆 17 处（含 startServiceForAction 死代码、通知文案
  HTTP 分支、START_STICKY 因子、specialUse 归 false）；EnhancedAIService/
  OperitApplication/ActivityLifecycleManager 启动门槛与 AutoStarter 摘除；
  OperitScreens 路由与 SettingsScreen 入口删除
- 前端/CI：web-chat/ 工程整删；根 package.json 删 build:webchat；.gitignore 两
  条目删除；android-build/pr-check/android-tests 三 workflow 的 npm 与 web 门控
  摘除；pr_check.py 删 web 车道（根 npm 配置不归属任何 CI 车道）；
  test_pr_check.py 两测试同步改写
- 文档/字符串：feature-protocol 三协议文档删除；BUILDING.md（环境说明、
  构建步骤 5/6 撤销并重编号、故障表行）、CONTRIBUTING.md（六处）、ci/README.md
  （两处）同步；八语 52 键（external_http 族 50 + 通知 HTTP 文案 2）清除
- 净变化：46 文件，约 +40/-15000
