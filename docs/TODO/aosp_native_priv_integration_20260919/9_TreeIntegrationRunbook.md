# 步骤 9：树侧集成 RUNBOOK（应用侧全部就绪后的接树手册）

> 前置状态（2026-09-22）：应用侧零遗留。122 Java/Kotlin prebuilt + quickjs/wamr/streamnative/ripgrep/sherpa
> 五个 native 模块全部以真 Android.bp 落地；CI 全绿（Kotlin 编译、sherpa/ripgrep .so 回写闭环）；
> ffmpeg fork 在组织仓 `ApertureLatticework/android_external_ffmpeg`（lineage-23.2 = e6285e1）。
> 本手册把树侧动作收敛为可照抄命令；两行翻牌完成后，ROM 专属特权应用线闭合。

## 1. local_manifests 就位

```bash
cd <你的 LOS 23.2 树根>
mkdir -p .repo/local_manifests
cp <local_manifests 仓>/aperture.xml .repo/local_manifests/
cp <local_manifests 仓>/upstream-ports.xml .repo/local_manifests/
```

注意：`aperture.xml` 钉 `packages/apps/Operit@lineage-23.2` 分支。本计划工作在
`feat/native-rom-integration`——二选一：
- **正式**：将该分支合入 `lineage-23.2` 后 sync
- **联调**：临时把 manifest 的 revision 改成 `feat/native-rom-integration`

## 2. 首次拉取（可部分同步）

```bash
repo sync -c -j8 packages/apps/Operit external/ffmpeg
```

（全量 `repo sync` 亦可；上面两仓是本计划新增，其余按你树的常态。）

## 3. 依赖解析首验（不进产物）

```bash
source build/envsetup.sh && lunch <你的目标>
m Operit
```

首验关注点：
- Soong 能否解析 122 个 prebuilt 模块（android_library_import/java_import）
- `libavfilter`/`libavdevice`/`libffmpeg_cli` 三模块存在（fork 补齐项）
- 若报 `libav*` 链接 `vendor` 违规 → fork 的 vendor 翻转未生效，核对 fork commit 是 e6285e1

## 4. 翻牌（已完成，2026-09-22，仓库 commit b37498b）

主模块与 ffmpeg 薄壳的 `.tree` 暂存名已在仓库内正式翻为 `Android.bp`——
全部前置依赖（external/ffmpeg 定制 4、136 prebuilt、native 五件、树内模块名）
经 dodge 树实编验明。树侧无需任何翻牌操作，sync 到 ≥ b37498b 即生效。

## 5. device.mk 接线

```make
PRODUCT_PACKAGES += Operit OperitProvider
# 特权白名单（系统侧安装路径，缺了 boot 时特权权限被拒）
PRODUCT_COPY_FILES += \
    packages/apps/Operit/etc/privapp-permissions-operit.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/privapp-permissions-operit.xml
```

（2026-09-23 起追加 OperitProvider：无障碍 provider 伴随 APK 源码入仓
（provider/，feat/accessibility-provider-opensource），platform 签名，
主应用不再内嵌 assets/accessibility.apk。）

## 6. 构建与刷机

```bash
m Operit && adb install -r $OUT/system/priv-app/Operit/Operit.apk   # 或整包 OTA 流程按你树常态
```

## 7. 四场景实测清单（真机，全部通过才算闭合）

| 场景 | 动作 | 判定 |
| --- | --- | --- |
| ACTION_MULTIPLE 文本注入 | 副屏打开任意输入框，跑打字自动化 | 中文/长文逐字上屏，无丢块（64 字符分批） |
| DRM 安全捕获 | 系统设置开"安全捕获"，触发截图 | CAPTURE_SECURE_VIDEO_OUTPUT 生效，画面非黑屏 |
| 开机自启存活 | 重启设备 | LiveService 起在通知栏；无障碍 provider 自动登记 |
| 显示引用计数 | 副屏会话开关 10 轮 | 无 VirtualDisplay 泄漏（dumpsys display 数量回落） |

## 8. 常见故障速查

| 症状 | 根因 |
| --- | --- |
| `module ... not found`（operit-* 系） | 未翻牌或 sync 的分支不含 prebuilt 入库提交（须 ≥ 67231fe） |
| `libavfilter` undefined | fork 分支不是 e6285e1 系（avfilter bp 是 fork 增补） |
| boot 时权限拒绝刷屏 | device.mk 漏了 privapp-permissions 的 COPY_FILES |
| 副屏触控无反应 | 步骤 7 的 INJECT_EVENTS 白名单未装或伴随 provider 未登记 |

## 9. 后续（另行排期，不阻塞本步骤）

- Live 三缺件：帧泵实时流、流式多模态通路、AST 查询工具化（用户已定方向未排期）
- 树内直引优化（okhttp/gson 等 12 项换树内模块名）：日后逐个换，非当前路径
- accessibility provider 源码入仓：✅ 已完成（provider/ 模块，见 ../accessibility_provider_sourcing_20260923/）
