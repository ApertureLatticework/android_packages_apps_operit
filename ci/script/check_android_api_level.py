#!/usr/bin/env python3
"""ANDROID_API_LEVEL / ps1 ApiLevel 一致性断言（单一事实源门禁）。

背景：同一 API 档在仓内四处独立字面声明——三份 workflow 的
ANDROID_API_LEVEL env + build_native_ripgrep.ps1 的 $ApiLevel 默认值。
981c0a9 之前的 26/23 错配（CI '26'×3 + ps1 23）就是靠人手扫出来的，
本断言把「改一处漏三处」变成 CI 即红：

  - 每份 workflow 必须恰好声明一次 ANDROID_API_LEVEL（漏声明/重复声明都红）
  - ps1 必须恰好声明一次 [int]$ApiLevel 默认值
  - 四处取值必须相等

取值本身（31/36）不归本脚本管（minSdk/targetSdk 是产品决策）；本脚本只
钉「同一档的多处声明一致」。

用法：
  python3 ci/script/check_android_api_level.py
"""
import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
WORKFLOWS = [
    ".github/workflows/android-build.yml",
    ".github/workflows/android-tests.yml",
    ".github/workflows/pr-check.yml",
]
PS1 = "tools/native_ripgrep/build_native_ripgrep.ps1"

WF_RE = re.compile(r"^  ANDROID_API_LEVEL:\s*'(\d+)'\s*$", re.MULTILINE)
PS1_RE = re.compile(r"\[int\]\$ApiLevel\s*=\s*(\d+)")


def main() -> int:
    declarations = {}
    failures = []
    for rel in WORKFLOWS:
        matches = WF_RE.findall((REPO_ROOT / rel).read_text(encoding="utf-8"))
        if len(matches) != 1:
            failures.append(f"[{rel}] ANDROID_API_LEVEL 声明 {len(matches)} 处（应恰好 1 处）")
            continue
        declarations[rel] = matches[0]
    ps1_matches = PS1_RE.findall((REPO_ROOT / PS1).read_text(encoding="utf-8"))
    if len(ps1_matches) != 1:
        failures.append(f"[{PS1}] ApiLevel 默认值声明 {len(ps1_matches)} 处（应恰好 1 处）")
    else:
        declarations[PS1] = ps1_matches[0]

    values = set(declarations.values())
    if len(values) > 1:
        for source, value in sorted(declarations.items()):
            failures.append(f"[不一致] {source} = {value}")

    if failures:
        print("\n".join(failures), file=sys.stderr)
        print("四处声明必须同值：统一改齐后再提交（缓存 key 随 env 自动换新）。", file=sys.stderr)
        return 1
    value = next(iter(values))
    print(f"[通过] 四处声明一致：ANDROID_API_LEVEL / ApiLevel = {value}（{len(declarations)} 处）")
    return 0


if __name__ == "__main__":
    sys.exit(main())
