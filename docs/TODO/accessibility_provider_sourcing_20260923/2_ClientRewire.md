# 2. 主应用接线收敛与安装器删除 [DONE]

## 旧实现情况

主应用把 provider 当外置包伺候：

- `AccessibilityProviderInstaller`：assets 提取 APK → FileProvider URI →
  ACTION_VIEW 安装意图；installedVersion 对比 bundledVersion 提示更新
- `UIHierarchyManager`：extractProviderApkFromAssets、launchProviderInstall、
  isProviderAppInstalled、isUpdateNeeded 四条安装线，绑定走包管理器服务发现

## 意图

ROM 分发后 provider 永远在场且同签名，安装/更新链路整删，绑定直连。

## 期待的新实现

- `AccessibilityProviderInstaller.kt` 删除（唯一职责被分发形态吸收）
- `UIHierarchyManager` 收敛：
  - extractProviderApkFromAssets / launchProviderInstall / isUpdateNeeded /
    isProviderAppInstalled 四法与调用面（设置页无障碍状态卡）同刀删除
  - bindToService 的包管理器服务发现改为显式 ComponentName 直钉
    `com.ai.assistance.operit.provider/.RemoteBinderService`（包名稳定，ROM 内建）
  - PROVIDER_MARKET_URL 等外置渠道痕迹清除
- 设置页无障碍卡面：版本行改显 OperitProvider 构建版本（PackageManager 读本机包），
  引导安装文案删除
- `app/src/main/assets/accessibility.apk`、`accessibility_version.txt` 删除；
  app Android.bp 的 assets 收集面同步
- ROM 侧：device.mk 增 `PRODUCT_PACKAGES += OperitProvider`

## 验证

- 全仓 rg 无 accessibility.apk / Installer 残留引用
- 设置页无障碍卡正常显示服务状态（绑定即绿）
- 主 APK 体积 -2.7MB
