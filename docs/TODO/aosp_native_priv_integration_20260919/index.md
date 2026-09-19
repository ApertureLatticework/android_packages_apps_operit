---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: feat/native-rom-integration
Status: planning
---

# 原生集成特权版：LineageOS 源码进树改造

## 原有状况

Operit 以 Gradle 构建产出通用 APK，系统能力通过五档权限阶梯借取：

- `ROOT` 档依赖 libsu 以 uid 0 执行 shell 命令
- `DEBUGGER` 档依赖 Shizuku 桥接 shell 身份
- `ADMIN`、`ACCESSIBILITY`、`STANDARD` 档各走对应通道
- 全库 120 条 Maven 依赖，其中 ObjectBox（3 实体）与 Room（约 7 实体）双轨并存，均依赖 kapt

代码证据：root 档实际执行的命令为 `input text/tap/swipe`、`pm install`、`run-as`、`settings put`、`am force-stop`，全部为 shell 级操作；iptables、remount /system、跨应用私有数据读取在功能代码中零使用（唯一 remount 字样出现在 DemoStateManager 的展示字符串）。

## 意图

改造为 LineageOS 系 ROM 独占的原生集成特权应用，只随 ROM 分发：

- 源码整体进树为 `packages/apps/Operit` 自包含包，全部依赖源码收入包内 `libs/`，以 Soong 模块直接引用
- platform 签名 + priv-app，ROOT 与 Shizuku 系执行器整档删除，新增 `PRIVILEGED` 档直调 SystemApi
- 解锁 Live 能力：免同意弹窗的持续屏幕采集（CAPTURE_VIDEO_OUTPUT）与 INJECT_EVENTS 直注入自动化
- ML Kit 为全树唯一 prebuilt 例外，其余依赖零二进制

## 作用域

- `app/src/main/java` 权限阶梯与数据层改造
- 包内 `libs/`、`native/` 的 Soong 模块化
- LOS fork 侧的 device.mk 接入与 privapp 白名单

## 非目标

- 不保留普通渠道（GitHub Release）双发形态
- 不做任何回退或降级路径，删即删净
- 不使用 LSPosed、Zygisk、KernelSU 及一切 hook 注入方案，ref/ 中仅取机制参考
- 不改动上游 Gradle 构建的存续，迁移期间 Gradle 构建必须全程保持可用，作为开发主循环直至 Soong 全通

## 决策记录

| 决策点 | 结论 |
| --- | --- |
| ROM 基础 | LineageOS 23.2（Android 16，树内 Kotlin 2.x，Compose 编译器已与运行时解耦） |
| 分发 | 仅随 ROM |
| ML Kit | 保留为全树唯一 prebuilt 例外 |
| Java 依赖迁移 | 先裁剪功能面再迁，非核心依赖不入树 |
| ObjectBox | 迁 Room 后整线删除 |
| Room APT | 生成类 check-in，Soong 零注解处理器 |
| 依赖组织 | 全部收进 packages/apps/Operit 包内，模块名 operit- 前缀避撞，visibility 限私有 |
| 前台抢占规避 | 参考 ref/ 副屏底座，禁用 LSPosed，控制服务可用 system rc 启动；LOS 23.2 源码验证 TRUSTED+OWN_FOCUS 原生旗标即可，无需 framework 补丁 |
| Linux 终端环境 | proot、chroot、lxc 全部裁掉，不写 root helper，与 phone use 零耦合 |

## 步骤

1. [功能面裁剪与依赖清单定稿](1_ScopeCutAndDependencyManifest.md)
2. [ObjectBox 迁移 Room 并删除整线](2_ObjectBoxToRoom.md)
3. [Room 生成代码 check-in 机制](3_RoomGeneratedCodeCheckin.md)
4. [权限阶梯改造与 PRIVILEGED 档](4_PrivilegedExecutorAndTierRemoval.md)
5. [包内 Soong 模块化与 Android.bp](5_InTreePackageAndSoongModules.md)
6. [LOS 接入、特权白名单与 Live 服务](6_LOSIntegrationAndLiveService.md)
7. [副屏底座原生化与前台抢占规避](7_VirtualDisplayBackend.md)
8. [提示词精简](8_PromptCleanup.md)
