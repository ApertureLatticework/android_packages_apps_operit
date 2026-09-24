# 2. 替换执行与树侧验证 runbook

> 本文档为执行手册：逐件走完"改 bp → 树内编译 → 钉板"闭环，不批量一把梭。

## 逐件流程

```bash
# 0. 前置：树内 sync 到最新 lineage-23.2，lunch 目标不变
# 1. 树内核对该件模块（判定表 ⚠️ 项终判；✅ 项复核分支漂移）
grep -r "name: \"<模块名>\"" external/<路径>/Android.bp

# 2. 本仓改引用：Android.bp 的 static_libs 中
#    "operit-<名>" → "<树内模块名>"；visibility 依赖树内模块是平台公开面，无围栏问题

# 3. 树内全量编译（依赖闭包变了必须全编，不能只编单模块）
m Operit

# 4. 运行面抽查：进设置页触发该库的网络/解析路径
adb install -r $OUT/system/priv-app/Operit/Operit.apk

# 5. 绿了再退役：删 libs/<模块>/ 目录，prebuilts.lock 同步
#    import_prebuilts.py 的 ROOTS 加排除（该件 Gradle 仍需要，但登记状态改"树内直引"）

# 6. libs/DEPS.md 登记表更新该件状态
```

## 失败处置

| 症状 | 处置 |
| --- | --- |
| `module ... not found` | 树内该分支无此模块（判定表 ⚠️ 项坐实），回滚该件引用，DEPS.md 钉包内并记根因 |
| 编译期 API 缺失（NoClassDefFound/method 不存在） | 版本漂移坐实，同上钉包内 |
| jarjar 撞包（bouncycastle 系高危） | 平台定制重打包不可直用，钉包内终局 |

## 退役完成的登记格式

DEPS.md 逐件状态行示例：

```
- gson：✅ 已直引树内 `gson`（2026-09-23，commit <树内短哈希>）
- apksig：❌ 终局钉包内——树内为 java_library_host，设备端不可引用
```

## 完成判据

判定表全部件落定 ✅/❌ 终态；包内 prebuilt 数量从 122 下降对应件数；
`m Operit` 全绿一次。
