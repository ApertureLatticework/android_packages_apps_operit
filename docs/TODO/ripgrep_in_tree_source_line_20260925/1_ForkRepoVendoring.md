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

## 勘误（2026-09-25）

「避让 AOSP 自有 external/ripgrep」说法有误——实查 AOSP 仓表（3130 仓）
零命中，树内并无 ripgrep 仓，该 path 空闲。manifest 与 fork README 注释
已修正；path 维持 external/operit-ripgrep 以自明归属。

## 形态备选（用户问询在案）

现形态为最小 vendor（7 crate 钉本仓 Cargo.lock 版本）。备选为整仓 fork
上游 BurntSushi/ripgrep（workspace 自带 globset/grep-*/ignore，裁无用成员），
代价是本仓 Cargo.lock 须对 fork tag 重锁对齐；regex 三件两种形态都需单独
vendor（属 rust-lang/regex 仓）。待用户定夺。

## 降级实录（2026-09-25）

用户树侧实情：android16-release 的 rustc 为 1.82/1.83，edition 2024
（1.85+ 特性）不可编。四件降 last-2021-edition 版：

| crate | 2024 版起点 | 钉定 |
| --- | --- | --- |
| globset | 0.4.17 | 0.4.16 |
| grep-matcher | 0.1.8 | 0.1.7 |
| grep-regex | 0.1.14 | 0.1.13 |
| ignore | 0.4.24 | 0.4.23 |

fork 仓 db79707 已换源；本仓 Cargo.lock 手工对齐（crates.io API 校验和，
依赖集零变化）。regex 三件（aho-corasick/regex-syntax/regex-automata）本就
edition 2021 不动。Gradle cargo 线随新锁编译，CI android-build 复验。
