# 上游 draw 批次吸收与 packages 漂移门禁重建（2026-09-26）

Status: 已落地，待树侧 `m Operit` 复核（PackageManager/ScriptExecutionDialog 编译 + APK 内 spacexai_draw.js）。

## 起因：同步审计（1.md，外部报告，全部核实）

- 上游 `AAswordman/Operit` **dev** 分支 `14c8afdc^..29d67812`（9/20 批次）三提交未吸收，
  且是**静默**漏吸：旧漂移门禁（`build:examples:github` + `examples/github.js`
  diff 门）随我方 `f936608c Drop examples`（压缩史前提交）消灭 examples/ 一同消失。
- 我方五包冻结在 `0f11d962`（9/18）：xai_draw/openai_draw/qwen_draw/
  siliconflow_draw/zhipu_draw 逐字节相同（blob sha 实证）。
- **坑**：`spacexai_draw` 不是重命名，是改名+分叉——上游删 `xai_draw.js`
  新写 `spacexai_draw.js`（+809 行：grok-imagine-image-2.0、1k/2k/4k、
  视频线），消费方 `packages_whitelist.txt` 同步换名。合并时按 rename
  对待会丢分叉内容。
- 分支事实：三提交在 **dev**（head=29d67812）；**main（dbf71916f，1.12.2
  发布线）没有 spacexai**，曾干扰判断。

## 吸收内容（全部取自 dev@29d67812 终态）

| 件 | 处置 |
| --- | --- |
| openai/qwen/siliconflow/zhipu 四包 | img2img（ef1bf4b6）+ openai multipart 上传修复（29d67812） |
| spacexai_draw.js | 新件入库；xai_draw.js 删除 |
| packages_whitelist.txt | `xai_draw.js` → `spacexai_draw.js` |
| ScriptExecutionDialog.kt | 上游 +8/-1 直取（我方与 0f11d962 逐字节同） |
| PackageManager.kt | +37 偏好迁移块手工移植（我方已分叉）：常量两条、`migrateRenamedPackagePreferencesIfNeeded()`、`loadAvailablePackages()` 后调用点 |
| examples/* | 不接（我方已砍该目录，新门禁不依赖） |

env 键 `XAI_API_BASE_URL` 上游未漂，与我方史一致。吸收后 31 件与
dev@29d67812 **全等**（逐件 blob sha 比对）。

## 门禁重建（接替死掉的 examples 门）

`tools/example_packages/upstream_pin.json`：钉上游 repo/branch/commit +
31 件 blob sha。`ci/script/check_package_drift.py` 两道检查，任一失败即红：

1. **本地完整性**：工作树 blob sha 逐件对 pin（新增/被改/被删都要与 pin
   同 commit 更新）；
2. **上游前进**：dev 分支 path=`app/src/main/assets/packages` 最新提交
   必须仍是 pin 所钉，否则列出其后新提交，逼评估移植。

接线：`android-build.yml`（Set up JDK 前一步）。依赖 gh（CI 内置/本地需
登录态）。旧 pr-check 只盯 development 分支 PR，我方直推 lineage-23.2
不触发，故不接那里。

## 常规流程

上游 dev 前进 → 门禁红并列出提交 → 评估移植（js 直取终态，Kotlin 对锚点
手工移植）→ `upstream_pin.json` 同 commit 更新。本地有意改动 packages/
同理。禁把 spacexai_draw 类分叉件当纯 rename 合并。

## 补记（2026-09-26，2.md 复审后）

- **已发布判定**（翻转 A/B）：三提交未被任何 tag 收（v1.12.2=9/18 封版，
  早于 dev 上 9/20 批次）；而 xai_draw 在 v1.12.2 内流通，本仓
  versionName 即 1.12.2（versionCode 51）——存量用户偏好里存的是
  "xai_draw"。故 PackageManager 偏好迁移块为**必选项**（469973d 已随批
  落地，含一次性键/新名落地前置判断/双键迁移三重防护）。
- 品牌线溯源：SpaceXAI 品牌改造（6b8fceeb，8/23）已在 v1.12.2 发布面
  （显示名/supportedEfforts/xai.svg），包改名是其下游收尾；Kotlin 品牌侧
  本仓经 f38d10a 已对齐。
- 文档腐坏收口：ci/README.md（ToolPkg 门禁描述→漂移门禁实况）、
  CONTRIBUTING.md（examples/ 路径与死链 TOOLPKG_FORMAT_GUIDE.md、四条
  死命令→sync_example_packages + check_package_drift）。
  package.json 先前已净（2.md 该点过期）。
- 门禁 CI 失败修复：Actions 内 gh 需 GH_TOKEN（补 env github.token）。

## 树侧终验（2026-09-26，cnb 云机 dodge，全绿）

`m Operit` 13m09s 成功（vendor/oneplus/dodge 缺 radio/modem.img 系树侧
blob 残缺，验证期临时注释其校验行后即还原；真机构建需补齐 extract）。
产物实证：

- APK 213MB，`assets/models/` **8 件全数来自专仓 android_library 合并**
  （static_libs 资产通路全链贯通）
- `spacexai_draw.js`（41062B）在、`xai_draw.js` 绝迹
- `lib/arm64/` 旁挂 `liboperit_ripgrep.dylib.so`→`/system/lib64/`（源码线
  4.9MB 实体）+ ffmpeg/quickjs/sherpa/streamnative/toolpkgwasm 六件套

专仓修复链（每颗都是树侧实测暴露）：be7cb05 补最小 manifest（soong 默认
在模块根找）→ be92901 树修复（blobless 无检出提交误清 assets/**，教训：
blobless 克隆提交前必 read-tree 全树）→ c3eec8c 补 package 声明
（manifest-merger 硬校验）→ 007f5b7 钉 sdk_version 31（无此声明时 fixer
盖平台 SDK 36 撞消费方 minSdk 31）。

CI 侧同日全绿：API 面对齐 31 后 android-build success（android31-clang
在 NDK 25.1.8937393 内，r25 unified 覆盖 21-33 预期成立）。
