# 6. LOS 接入、特权白名单与 Live 服务

## 旧实现情况

- 屏幕采集仅有 MediaProjection 单次授权模式（manifest 已声明 FOREGROUND_SERVICE_MEDIA_PROJECTION）
- 操作通道为无障碍手势，延迟与覆盖面受限
- 应用随 GitHub Release 分发，签名自持

## 意图

接入 LOS fork，落地 Live：免弹窗持续看屏、多模态决策、双通道执行的手机自动化。

## 期待的新实现

接入：

- repo manifest 增加 packages/apps/Operit 仓库
- device.mk 或 vendor/lineage/config/common.mk 增加 `PRODUCT_PACKAGES += Operit`
- privapp-permissions 白名单收录 CAPTURE_VIDEO_OUTPUT、CAPTURE_SECURE_VIDEO_OUTPUT、INJECT_EVENTS、WRITE_SECURE_SETTINGS、INSTALL_PACKAGES、FORCE_STOP_PACKAGES、ADD_TRUSTED_DISPLAY、INTERNAL_SYSTEM_WINDOW 等条目

Live 服务：

```
前台服务，开机自启
└─ 主屏观察模式：VirtualDisplay(AUTO_MIRROR) → ImageReader 定频取帧
└─ 副屏执行模式：见步骤 7，Agent 在 headless 副屏操作，主屏无感
    → 降采样 → MNN-VL 或云端多模态
    → 决策
    → 执行：INJECT_EVENTS 直注入 + 无障碍语义树融合定位
```

- 帧泵与模型侧复用现有云端 Provider 通道（本地 mnn/llama 已随步骤 4 切割出仓）
- 安全页面采集依赖 CAPTURE_SECURE_VIDEO_OUTPUT，默认关闭可配置

## 作用域

- LOS fork 的 manifest 与 product 配置
- Operit 侧 Live 前台服务、帧泵、决策到执行的闭环
- privapp 白名单与开机自启链路

## 验证

- Spike 1（先行）：静态验证已完成，交付物在 [spike1/](../spike1/)；Soong compose 插件管线、树内 androidx Compose 模块名、SystemUI/Settings/Car SystemUI 先例均已确认，剩余树内实测项与失败处置见 spike1/RUN.md。Android 16 树内 Kotlin 预期为 2.x，实测后回填
- Live 全链路：目标应用界面导航、输入、安装、强停四类场景实测
- 重启与长期后台存活

## 风险

- CAPTURE_SECURE_VIDEO_OUTPUT 在部分设备上受 DRM 策略限制，按设备实测调整默认值

## 执行情况（2026-09-20 应用侧落地）

- `LiveScreenMirror`（core/tools/system/live/）：AUTO_MIRROR 虚拟显示器 + ImageReader，
  引用计数生命周期，captureFrame 等待首合成周期；SECURE 旗标由显示设置
  live_secure_capture 开关控制（默认关，GlobalDisplaySettingsScreen 入口，八语同步）
- `LiveService` + `LiveBootReceiver`（services/live/）：specialUse 前台服务开机自启
  （BOOT_COMPLETED + 应用启动双入口，特权档判定收敛在 ensureStarted）；
  服务创建时 AccessibilityAutoEnable 直写 enabled_accessibility_services 登记无障碍
  provider（步骤 4 遗留项闭环，非 priv 环境显式失败走手动引导）
- manifest：八条特权权限（tools:ignore ProtectedPermissions）+ Live 服务/接收器声明
- 档位默认：preferredPermissionLevelFlow 未设置时默认 PRIVILEGED（ROM-only 本位档）
- 采集通道分路：特权档主屏 PhoneAgent 截屏与 capture_screenshot 工具
  （PrivilegedUITools 覆写）走镜像免弹窗直采；副屏会话归步骤 7；
  原跨屏兑底采集（副屏截不到回退主屏截）同刀删除

LOS fork 侧接入片段（树内操作，非本仓文件）：

repo manifest（局部）：

```xml
<project path="packages/apps/Operit"
         name="ApertureLatticework/android_packages_app_operit"
         revision="<集成分支>" />
```

device.mk 或 vendor/lineage/config/common.mk：

```make
PRODUCT_PACKAGES += Operit
PRODUCT_COPY_FILES += \
    packages/apps/Operit/etc/privapp-permissions-operit.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/privapp-permissions-operit.xml
```

## 待实测项

- 树内 privileged + platform 签名后 priv-app 落位与开机自启链路
- Live 四场景（导航/输入/安装/强停）与重启长期存活
- DRM 页面在具体设备上的 CAPTURE_SECURE_VIDEO_OUTPUT 行为
