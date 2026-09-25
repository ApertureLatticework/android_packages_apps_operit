# 1. fork 仓 vendoring 与 Android.bp 模块群

## 仓内容（2026-09-25 实证收窄：AOSP android-crates-io 对账后仅 7 件）

闭包 35 件（滤 windows）中树内已有 26 件直接借用，含 jni 0.21.1（同版本）、
serde 1.0.219 / serde_json 1.0.140（1.x 稳定线）、memchr/bstr/walkdir/
same-file/crossbeam 系/thiserror 2/combine/cesu8/itoa/log 等。

fork 仓仅 vendor（android-crates-io 实查缺失或断代）：

| crate | 版本 | 原因 | 模块名 |
| --- | --- | --- | --- |
| globset | 0.4.18 | 树内无 | liboperit_globset |
| grep-matcher | 0.1.8 | 树内无 | liboperit_grep_matcher |
| grep-regex | 0.1.14 | 树内无 | liboperit_grep_regex |
| ignore | 0.4.27 | 树内无 | liboperit_ignore |
| aho-corasick | 1.1.4 | 树内 0.7 断代 | liboperit_aho_corasick |
| regex-syntax | 0.8.11 | 树内 0.6 断代 | liboperit_regex_syntax |
| regex-automata | 0.4.14 | 树内 0.1 断代 | liboperit_regex_automata |

注意事项（实证）：
- 树内 log 模块名是 `liblog_rust`（避让 C liblog），不是 liblog
- globset/grep-*/ignore 为 edition 2024，需树内 rustc ≥1.85
  （Android 16 预置满足概率高；不满足则该四件降 last-2021-edition 版）
- 测试/基准/示例目录不入仓，features 显式列出不赌 cargo default

## 模块命名

遵循树内 crates 惯例前缀 `lib`：libglobset、libgrep_matcher、libgrep_regex、
libignore、libjni、libserde、libserde_json…（最终名以树内无撞名为准，
创建时 `m nothing 2>&1 | grep module` 复核）。

## 版本纪律

Cargo.lock 为唯一真源：fork 仓各 crate 版本必须与本仓
`tools/native_ripgrep/Cargo.lock` 完全一致，runbook 记同步命令。
