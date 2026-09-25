# 2. 本仓翻牌与树侧验证 runbook

## 前置

- fork 仓已建并推组织仓
- local_manifests `upstream-forks.xml` 已增 `external/operit-ripgrep` 条目（✅ 11c43ab）

## 本仓改动

1. `Android.bp` 增：

```json
rust_library_dylib {
    name: "liboperit_ripgrep",
    crate_name: "operit_ripgrep",
    srcs: ["tools/native_ripgrep/src/lib.rs"],
    rustlibs: [
        "libglobset", "libgrep_matcher", "libgrep_regex",
        "libignore", "libjni", "libserde", "libserde_json",
    ],
    visibility: ["//packages/apps/Operit:__subpackages__"],
}
```

2. 删 `app/src/main/jniLibs/Android.bp` 整文件（cc_prebuilt 让名），
   `jniLibs/arm64-v8a/liboperit_ripgrep.so` 文件保留（Gradle 线消费）
3. Operit 的 `jni_libs`/`required` 条目不变，解析自动切到源码模块

## 树侧验证

```bash
source build/envsetup.sh && lunch <目标>
m liboperit_ripgrep      # 产物 out/.../liboperit_ripgrep.so
m Operit                 # 全绿复验
```

- 导出符号比对：`nm -D` 两条线产物，JNI 入口集合一致
- 真机走查：rg 内容搜索工具一次完整调用
