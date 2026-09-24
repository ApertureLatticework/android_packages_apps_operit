# 1. Kotlin 线删除 [DONE]

## 旧实现情况

整删四包 + prefs + UI + 六处接线：

- `integrations/http/`：ExternalChatHttpServer（547 行级）、WebChatHttpBridge
  （2900 行级）、WebChatModels、ExternalChatHttpState、ExternalChatHttpNetworkInfo、
  ExternalChatHttpAutoStarter、bridge/ 四件（Action/MemorySelector/InputSettings/Management）
- `integrations/a2a/`：A2aTaskManager、A2aHttpHandler
- `integrations/intent/ExternalChatReceiver.kt` + manifest 381-388 receiver 块
- `integrations/externalchat/`：RequestExecutor、ResponseSanitizer、Models
- `data/preferences/ExternalHttpApiPreferences.kt`
- `ui/features/settings/screens/ExternalHttpChatSettingsScreen.kt`

接线点摘除：

- AIForegroundService：imports、companion 的 externalHttpStateFlow、
  ensureRunningForExternalHttp/stopExternalHttp、shouldContinue 服务常驻判定中
  externalHttpEnabled 因子、实例的 preferences/monitorJob/server/currentPort、
  startOrRefreshExternalHttpServer/stopExternalHttpServer/updateExternalHttpState 族
- EnhancedAIService startAiService：externalHttpEnabled 读取与条件
- OperitApplication：externalHttpEnabled 读取与条件
- ActivityLifecycleManager：AutoStarter import 与 ensureRunningIfEnabled 调用
- OperitScreens：import、navigateToExternalHttpChatSettings lambda、
  ExternalHttpChatSettings 路由 object 与 composable 分支
- SettingsScreen：参数与设置项 onClick

## 意图修正

外部入口不复存在；服务常驻与启动门槛只保留 alwaysListening/后台保活语义。

## 期待的新实现

上述文件与符号全删，无替代物；不写任何回退分支。
