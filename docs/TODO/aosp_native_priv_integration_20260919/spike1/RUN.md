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
