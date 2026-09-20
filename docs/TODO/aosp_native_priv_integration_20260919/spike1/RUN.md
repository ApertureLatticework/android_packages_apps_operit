# Spike 1：LOS 23.2 树内 Compose 编译验证

## 目的

验证 LineageOS 23.2 树内 `compose: true` 编译链与树内 androidx Compose 运行时，为步骤 5 定 Compose 策略。

## 静态验证结论（已完成，本目录交付前）

- Soong `java/kotlin.go` 存在完整 compose 插件管线（composePluginFlag 注入 kotlinc 命令模板），`java/java.go` 有 compose-plugin 工具链依赖
- 树内已有 Compose 使用先例：SystemUI、Settings、Car SystemUI
- 树内 androidx Compose 模块名确认：`androidx.compose.runtime_runtime`、`androidx.compose.ui_ui`、`androidx.compose.material3_material3`
- 树内另有 `androidx.room_room-compiler-plugin`（javac 通路），与步骤 3 相关

## 树内执行步骤

1. 拷贝本目录到树内任意位置，例如 `packages/apps/SpikeCompose/`
2. 执行：

```bash
source build/envsetup.sh
lunch <你的设备 lunch 目标>
m spike-compose
```

3. 记录构建日志中的 kotlinc 版本行与 compose 插件 jar 路径

## 通过标准

- `m spike-compose` 成功产出 APK
- kotlinc 版本为 2.x（决定 vendored Compose BOM 的兼容窗口，Kotlin 2.0 起编译器与运行时解耦）

## 失败处置

- 属性名报错时，在树内执行 `grep -rn "composePlugin" build/soong/java/*.go` 与 `grep -rn "Compose bool" build/soong/` 定位当前属性拼写后修正 Android.bp
- static_libs 找不到时，执行 `grep -o '"androidx.compose[^"]*"' out/soong/module-info.json | sort -u` 核对模块名

## 结果回填

完成后在本文件追加：构建日期、lunch 目标、kotlinc 版本、插件路径、APK 路径、是否需修订步骤 5。

## 2026-09-19 实测翻车记录与修正

用户树内实跑：`error: packages/apps/operit/Android.bp:7:12: unrecognized property "compose"`。
当初的静态验证只坐实了树内 Compose 模块名（androidx.compose.runtime_runtime 等在
SystemUI 的 static_libs 里），没验证 `compose: true` 属性本身——LOS 23.2 Soong 无此属性。

已实证的正确形态（SystemUI frameworks/base/packages/SystemUI/Android.bp）：
- Compose 模块直接进 static_libs，无任何专用属性
- kotlincflags 携带 `-P plugin:androidx.compose.compiler.plugins.kotlin:...` 插件参数
- SystemUI 的 `plugins:` 列表只有 room/dagger，Compose 编译器插件本体的挂载点仍未知

待树内 grep 定位（见会话记录）：
1. prebuilts/androidx 内 compose 编译器插件的模块名与类型（java_plugin?）
2. 其它 compose 消费者 bp 的完整挂载写法

## 结论定稿（同日，用户树内 grep + LOS 镜像范本双证）

- LOS 23.2 的 androidx 预编译在 `prebuilts/sdk/current/androidx/`（manifests 亦在此）
- Compose 编译器 jar 定义于 `external/kotlin-compose-compiler/`（2.2.0，java_import_host 双形态）
- Soong 的 Kotlin 编译驱动 `build/soong/cmd/kotlinc_incremental/` static_libs 内嵌
  `kotlin-compose-compiler-embeddable` → 插件对全树 Kotlin 模块默认常驻，无属性无声明
- 官方范本 `development/samples/SceneTransitionLayoutDemo/Android.bp`（LOS 23.2 版）：
  static_libs 引 Compose 模块 + kotlincflags 带 -P 可选参数，仅此而已

spike1/Android.bp 已按范本对齐重写，待树内 `m spike-compose` 终验。

## 终验通过（2026-09-19，用户树内实测）

`m operit`（含 spike 同款配置的 operit 模块）编译全绿：
out/target/product/dodge/system/app/operit/ 出 odex 产物，4949/4949。

Spike 1 三连坑全记录（后续模块化的既定规则）：
1. `compose: true` 属性不存在 → Compose 模块直接进 static_libs
2. sdk_version 空置报冲突 → platform_apis: true（特权应用路线）
3. manifest 缺 package 属性 → 注入 package="com.ai.assistance.operit"

当前产物落位 system/app/（未设 privileged），步骤 6 补 privileged: true
+ certificate: "platform" 后迁入 system/priv-app/。
