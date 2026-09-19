# 4. 权限阶梯改造与 PRIVILEGED 档

## 旧实现情况

`ShellExecutorFactory` 与 `ActionListenerFactory` 维护五档阶梯：

```
ROOT(libsu) > ADMIN > DEBUGGER(Shizuku) > ACCESSIBILITY > STANDARD
```

每档各自实现 ShellExecutor 与 ActionListener。root 档实际命令面为 input 注入、pm install、run-as、settings put、am force-stop，均为 shell 级操作，SystemApi 均有对应。

## 意图

ROM 独占后借权通道整档删除，阶梯收缩为三档，特权档直调 SystemApi。

## 期待的新实现

- 新增 `PrivilegedShellExecutor` 与 `PrivilegedActionListener`：
    - input 注入 → `INJECT_EVENTS` 直调 InputManager
    - pm install → `INSTALL_PACKAGES` 与 PackageInstaller 会话
    - settings put → `WRITE_SECURE_SETTINGS` 直写
    - am force-stop → `FORCE_STOP_PACKAGES`
    - run-as 场景按实际用途逐条改写为对应 SystemApi 调用
- 阶梯改为 `PRIVILEGED > ACCESSIBILITY > STANDARD`，工厂按新序取档
- 删除项：libsu 依赖、shizuku 依赖、`RootShellExecutor`、`RootAuthorizer`、`RootActionListener`、Shizuku 系执行器与授权器、ShizukuInstaller、manifest 中 Shizuku 权限与 queries 声明
- UI 同步删除：向导卡片 RootWizardCard 与 ShizukuWizardCard、PermissionGuideScreen 中的 ROOT 与 Shizuku 系档位条目、演示界面对应卡片
- 无障碍自启：platform 签名后写 `enabled_accessibility_services`，去掉手动开启引导

## 作用域

- `core/tools/system/shell/`、`core/tools/system/action/`、授权器与安装器
- `AndroidPermissionLevel` 与两处工厂
- `AndroidManifest.xml` 权限区
- 设置界面的权限引导文案与入口

## 并入事项：本地 AI 运行线切割（2026-09-19 决策）

语音链路为模型无关管线（STT→任意 LLM→TTS），本地推理对语音零影响。切割面：

- 删除：llm/llama 与 llm/mnn 两 Gradle 模块（llama.cpp 与 MNN-LLM JNI）、LlamaProvider、MNNProvider、LocalGenerationEnd、MnnModelDownloadManager、MnnModelDownloadScreen 及全部路由
- 删除：sherpa-mnn 线（SherpaMnnSpeechProvider 与 com/k2fsa/sherpa/mnn/，MNN 后端 ASR；其 Vad 类无外部消费）
- 删除：ApiProviderType.MNN/LLAMA_CPP 枚举、ModelConfigData 的 mnn*/llama* 字段、保存管线（ModelConfigManager/ApiAutoSaveState/SoftwareSettingsModify 工具参数）与双语 schema 中对应条目
- 保留：sherpa-ncnn（SHERPA_NCNN ASR + 唤醒词预滚）、OnnxSileroVad（onnxruntime，唤醒词 VAD 本体）、PersonalWake* 全套、云 STT/TTS 各 provider
- STT 面收敛为：本地 sherpa-ncnn / OpenAI / Deepgram
- Soong 侧受益：卸掉 llama.cpp 与 MNN 两个 C++ 大库，保留 sherpa-ncnn（+ncnn）与 onnxruntime

## 验证

- 原 root 档命令面的逐条对照测试：每条命令在新档有等价行为
- 无 root、无 Shizuku 环境下全功能回归

## 说明

- 本步骤全部改动保持 Gradle 构建可用，作为后续 Soong 化前的开发主循环
- 无障碍服务本体保留，作为语义树读取通道与注入的融合定位来源
