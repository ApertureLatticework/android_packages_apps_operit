---
Repository: https://github.com/ApertureLatticework/android_packages_apps_operit
Branch: docs/intree-direct-ref-plan
Status: 计划产出——按用户决策本轮只写方案不动代码
---

# 树内直引优化：包内 prebuilt 换 LOS 树内模块

## 原有状况

包内 prebuilt 21 件（见 libs/）对应的坐标在 LOS 23.2 树内有源码仓
（default.xml 实证路径表见步骤 1），目前 Gradle 与 Soong 消费同一份包内二进制。

## 意图

蹭 ROM 全局升级红利：能直引的换成树内模块名，包内 prebuilt 逐件退役；
不能直引的记录根因，钉在包内。

## 期待的新实现

不动 Gradle 线（Gradle 无树内模块概念，继续包内 prebuilt），只改 Soong 侧
`Android.bp` 的 `static_libs` 引用与 `libs/DEPS.md` 登记状态。

## 作用域

- 本仓 `Android.bp`、`libs/`（退役件删除与 lock 更新）
- `tools/intree/import_prebuilts.py` 的 ROOTS 排除表

## 非目标

- 不追求全部换树内（host-only、版本漂移、模块缺失者留包内并记录）
- 不在本轮动任何代码——计划先行，树侧验证后逐件执行

## 步骤

1. [模块名映射与逐件判定](1_ModuleMapping.md)（AOSP gitiles + LOS 23.2 manifest 实证）
2. [替换执行与树侧验证 runbook](2_SwapRunbook.md)

## 决策记录

| 决策点 | 结论 |
| --- | --- |
| 本轮范围 | 只写计划；代码执行待树侧逐件验证 |
| Gradle 线 | 不动，继续包内 prebuilt |
| host-only 模块 | apksig 为 java_library_host，设备端不可直引，钉包内 |
