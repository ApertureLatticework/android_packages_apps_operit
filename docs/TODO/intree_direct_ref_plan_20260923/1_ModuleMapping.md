# 1. 模块名映射与逐件判定

## 方法

- 路径存在性：/tmp 抓取的 LOS 23.2 default.xml 全量核对
- 模块名与形态：AOSP gitiles `refs/heads/main` 的 Android.bp 实读
  （LOS 23.2 的 remote="aosp" 仓与 AOSP main 同源；**执行时须在树内
  `m nothing 2>&1 | grep module` 或直读树内 bp 复核**，main 与 lineage-23.2
  分支可能漂移）

## 逐件判定表

| 包内 prebuilt | 树内路径 | 树内模块名 | 判定 |
| --- | --- | --- | --- |
| operit-okhttp | external/okhttp | `okhttp` | ✅ 直引候选；注意树内有 jarjar/repackage 变体，引用裸 `okhttp` 前核对 dest jarjar 规则不撞包名 |
| operit-okio | external/okio | `okio-lib` | ✅ 直引候选；okhttp 传递自带的 okio 与之争位，两件同批换 |
| operit-gson | external/gson | `gson` | ✅ 直引候选 |
| operit-jsoup | external/jsoup | `jsoup` | ✅ 直引候选 |
| operit-zxing-core | external/zxing | `zxing-core` | ✅ 直引候选 |
| operit-nanohttpd | external/nanohttpd | `libnanohttpd`、`nanohttpd-webserver`、`nanohttpd-websocket` | ✅ 按消费面选：本仓起本地 server，预期 `nanohttpd-webserver`（执行时核 Gradle 导入的类归属） |
| operit-commons-compress | external/apache-commons-compress | `apache-commons-compress` | ✅ 直引候选 |
| operit-commons-io | external/apache-commons-io | `apache-commons-io` | ✅ 直引候选 |
| operit-apksig | tools/apksig | `apksig` | ❌ **java_library_host 仅 host 工具可用**，设备端 APK 不可引用；钉包内，此判定终局 |
| operit-tensorflow-lite | external/tensorflow | 树内仅 C++ 面（`libtflite*` 等 cc 模块） | ⚠️ Maven `org.tensorflow:tensorflow-lite` AAR 的 Java API 等价 android_library 树内未见；执行时在树内搜 `tensorflow-lite` java 模块，无则钉包内 |
| operit-accompanist-drawablepainter | external/accompanist | 根 bp 仅 `subdirs = ["permissions"]` | ❌ 树内只有 permissions 线；drawablepainter 钉包内 |
| operit-accompanist-systemuicontroller | 同上 | 同上 | ❌ 同上；且 Compose 1.7+ 已内置 WindowInsetsController 方案，远期可考虑去依赖而非换树 |
| operit-okhttp-logging-interceptor | external/okhttp | 未见独立模块（源在 okhttp 仓内） | ⚠️ 树内合体或缺失，执行时核 `okhttp` 模块是否含 interceptor 类；无则两件 interceptor/sse 钉包内 |
| operit-okhttp-sse | 同上 | 同上 | ⚠️ 同上 |
| operit-commons-codec | external/apache-commons-… | manifest 有 bcel/lang/math 等，codec 仓未在 23.2 manifest 命中 | ⚠️ 执行时树内 `ls external | grep codec` 终判 |
| operit-commons-collections4 | 同上 | 未命中 | ⚠️ 同上 |
| operit-commons-lang3 | external/apache-commons-lang | 模块名待树内核（lang 与 lang3 世代差） | ⚠️ 树内是 commons-lang 2.x 世代的可能性高，API 不兼容即钉包内 |
| operit-commons-math3 | external/apache-commons-math | 同上 | ⚠️ 同 lang3 |
| operit-bouncycastle | external/bouncycastle | `bouncycastle`、`bouncycastle-bcpkix-unbundled` 等 | ⚠️ 本仓非根坐标（传递闭包件）；树内 bouncycastle 为平台定制版（jarjar 重打包），**包名与 Maven 原版不同**，直引大概率编不过——钉包内倾向，执行时终判 |
| operit-ktor-client-okhttp | 无（Kotlin ktor 线不在树） | — | ❌ 钉包内，终局 |
| operit-kotlinx-io-core / -bytestring | 无 | — | ❌ 钉包内，终局 |

## 版本纪律

每件替换前读树内模块对应源码版本（git tag 或 bp 内注释），与本仓
`libs.versions.toml`/`app/build.gradle.kts` 版本对照：minor 漂移可接受，
major/API 面漂移则该件钉包内。Soong 与 Gradle 行为差异以树内实测为准。

## 优先序建议

收益大风险小的先行：gson、jsoup、zxing-core、commons-io、commons-compress
（纯工具库、API 面 20 年稳定）→ okhttp/okio 两件同批 → nanohttpd →
其余按判定表终局钉死或条件成熟再换。
