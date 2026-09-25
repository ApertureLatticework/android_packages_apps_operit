# 1. fork 仓 vendoring 与 Android.bp 模块群

## 仓内容

- ripgrep workspace 依赖子集（按 `tools/native_ripgrep/Cargo.lock` 钉版本）：
  globset、grep-matcher、grep-regex、ignore（含 log/regex/regex-automata/
  regex-syntax/walkdir 等传递依赖）
- jni 0.21、serde 1（derive）、serde_json 1
- 每 crate 目录一个 `rust_library` 模块，`crate_name` 取包名，
  `visibility: ["//packages/apps/Operit:__subpackages__"]`
- proc-macro crate（serde_derive 等）以 `rust_proc_macro` 模块承载

## 模块命名

遵循树内 crates 惯例前缀 `lib`：libglobset、libgrep_matcher、libgrep_regex、
libignore、libjni、libserde、libserde_json…（最终名以树内无撞名为准，
创建时 `m nothing 2>&1 | grep module` 复核）。

## 版本纪律

Cargo.lock 为唯一真源：fork 仓各 crate 版本必须与本仓
`tools/native_ripgrep/Cargo.lock` 完全一致，runbook 记同步命令。
