# 7. 副屏底座原生化与前台抢占规避

## 旧实现情况

仓库 `ref/` 内嵌 agent-mobile-use（独立 git 子仓库，ColorOS 16 + KSU + LSPosed 实现验证），提供完全静默的后台自动化底座：

- Headless 虚拟副屏（Display > 0）承载目标应用，主屏正常使用甚至息屏
- LSPosed 注入 system_server 拦截 `ActivityTaskSupervisor` 与 `ActivityRecord` 的跨屏焦点约束，禁止副屏应用抢主屏焦点
- KSU root 运行 daemon：`agent_vd.dex`（app_process 创建副屏）、`agent_tools.dex`（vd CLI 八件套）、`vd_server`（Go 静态 HTTP 网关 3070 端口）
- 文字注入走无障碍 `ACTION_SET_TEXT`，不拉软键盘
- 触控注入走 `input -d <displayId>`
- 开机自启依赖 KSU 模块 `service.sh`

已知限制：加固应用（如微信）在无真实触摸的虚拟屏上无障碍节点被系统压制，`vd tree` 为空树，需以截屏视觉推理为主链路。

## 意图

将 ref/ 的机制在 LOS 23.2 源码中原生化，作为 Live 的执行面：Agent 在副屏操作，主屏完全无感。禁用 LSPosed，一切 hook 以 ROM 源码改动实现；控制服务允许以 system rc 启动以规避应用层限制。

## 期待的新实现

机制映射：

| ref/ 机制 | KSU/LSP 时代 | LOS 23.2 原生实现 |
| --- | --- | --- |
| 副屏创建 | root app_process daemon | priv-app 直调 DisplayManager，`ADD_TRUSTED_DISPLAY` 授予 `VIRTUAL_DISPLAY_FLAG_TRUSTED`，副屏被系统视为可信真实屏 |
| 焦点抢占拦截 | LSPosed hook system_server | 原生旗标 `VIRTUAL_DISPLAY_FLAG_OWN_FOCUS`，副屏独立焦点域，主屏焦点不受影响（LOS 23.2 源码验证，无需 framework 补丁） |
| vd CLI | /system/bin/vd + BOOTCLASSPATH 注入 dex | Soong 二进制，树内构建类路径天然正确，BOOTCLASSPATH hack 消失 |
| HTTP 网关 | KSU nohup vd_server | init rc 声明的 service 启动，system rc 规避应用层 BAL 与沙箱限制 |
| 触控注入 | input -d shell 调用 | `INJECT_EVENTS` 直调，携带 displayId |
| 文字静默注入 | agent_tools.dex 无障碍调用 | Operit 自留无障碍档直接 `ACTION_SET_TEXT` |
| 开机自启 | KSU service.sh | init rc 服务声明 |

设计约束：

- 禁用 LSPosed、Zygisk、KernelSU，ref/ 仅作机制参考不携带其代码与产物
- 副屏生命周期由 Operit 主导，rc 服务只承担被委托的常驻职责
- 副屏无障碍树被压制的应用走截屏视觉主链路，沿用 ref/ 实测结论
- 可选增强：`VIRTUAL_DISPLAY_FLAG_ALWAYS_UNLOCKED`（需配合 OWN_DISPLAY_GROUP）使锁屏状态下副屏任务持续

LOS 23.2 源码验证依据（lineage-23.2 分支实拉）：

- `DisplayContent.java:6727` hasOwnFocus()：`FLAG_OWN_FOCUS` 使显示器自带焦点域
- `DisplayManager.java:501` VIRTUAL_DISPLAY_FLAG_OWN_FOCUS 文档：单屏级焦点隔离，要求 trusted
- `DisplayManagerService.java:2007` 非 system uid 的 TRUSTED flag 强制校验 ADD_TRUSTED_DISPLAY
- `ActivityTaskSupervisor.java:1294` isCallerAllowedToLaunchOnDisplay：持 INTERNAL_SYSTEM_WINDOW 可启动到任意屏，untrusted 屏被整类拒绝
- `config.xml:2759` config_perDisplayFocusEnabled 默认 false，保持不动，零 overlay 零代码补丁

## 作用域

- packages/apps/Operit 内新增 vd 控制服务与 init rc 文件
- 旧 Shower 后端的原子切换：删除 ShowerController、ShowerServerManager、ShowerBinderRegistry、ShowerVideoRenderer、ShowerBinderReceiver、OperitShowerShellRunner、ShowerSurfaceView、showerclient 模块与 desktop.apk，PhoneAgent、StandardUITools、PackageManager、ActivityLifecycleManager、autoglm 工具页同步重接原生副屏（TRUSTED+OWN_FOCUS），或按功能裁剪决策一并删除
- Live 服务与副屏底座的执行链对接（与步骤 6 衔接）
- 不涉及 frameworks/base 改动，无需维护 framework fork

## 验证

- ref/ README 所列场景复现：主屏视频不中断、副屏应用操作、105 步连续手势级注入
- 息屏状态下副屏任务持续执行
- 加固应用走视觉链路的端到端用例

## 说明

- ref/ 目录本身不进入 LOS 树，机制吸收后在本文档登记对应原生实现
- 与步骤 6 的分工：6 负责主屏观察模式与权限面，本步骤负责副屏执行模式，Live 服务同时挂两条通道
