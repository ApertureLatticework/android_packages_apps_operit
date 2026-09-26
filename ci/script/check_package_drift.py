#!/usr/bin/env python3
"""packages 资产漂移门禁（接替随 examples/ 一同砍掉的旧 github.js diff 门）。

背景：app/src/main/assets/packages/ 与上游 AAswordman/Operit dev 分支同源，
2026-09-20 批次曾静默漏吸三提交（14c8afdc/ef1bf4b6/29d67812），暴露出门禁
真空。本脚本两道检查，任一不过即失败（无兜底）：

  1. 本地完整性：工作树内每个 packages/*.js 的 git blob sha 必须与
     tools/example_packages/upstream_pin.json 逐件一致（新文件/被改/被删
     都要求与 pin 同 commit 更新，防止无记录漂移）。
  2. 上游前进：dev 分支 path=app/src/main/assets/packages 的最新提交必须
     仍是 pin 所钉 commit；否则列出 pin 之后的新提交，要求评估移植并更新 pin。

依赖 gh（GitHub CLI，需登录态；CI 内置）。

用法：
  python3 ci/script/check_package_drift.py
"""
import json
import subprocess
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
PIN_PATH = REPO_ROOT / "tools/example_packages/upstream_pin.json"
PACKAGES_DIR = REPO_ROOT / "app/src/main/assets/packages"
UPSTREAM_PATH = "app/src/main/assets/packages"


def run_json(args):
    result = subprocess.run(args, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"[命令失败] {' '.join(args[:4])}...\n{result.stderr}", file=sys.stderr)
        sys.exit(1)
    return json.loads(result.stdout)


def main() -> int:
    pin = json.loads(PIN_PATH.read_text(encoding="utf-8"))
    failures = []

    local_files = {str(f.relative_to(REPO_ROOT)): subprocess.run(
        ["git", "hash-object", str(f)], capture_output=True, text=True, check=True
    ).stdout.strip() for f in sorted(PACKAGES_DIR.glob("*.js"))}

    for path in sorted(set(local_files) | set(pin["files"])):
        local_sha = local_files.get(path)
        pinned_sha = pin["files"].get(path)
        if local_sha != pinned_sha:
            label = "新增未入 pin" if pinned_sha is None else ("被删未入 pin" if local_sha is None else "与 pin 不符")
            failures.append(f"[本地] {path}: {label}")

    commits = run_json([
        "gh", "api", f"repos/{pin['repo']}/commits",
        "-f", f"path={UPSTREAM_PATH}", "-f", f"sha={pin['branch']}", "-f", "per_page=30",
        "--method", "GET",
    ])
    upstream_head = commits[0]["sha"] if commits else None
    if upstream_head != pin["commit"]:
        if not any(c["sha"] == pin["commit"] for c in commits):
            failures.append(f"[上游] pin {pin['commit'][:9]} 不在 {pin['branch']} 近 30 条路径提交内，差距过大需人工核对")
        else:
            for c in commits:
                if c["sha"] == pin["commit"]:
                    break
                message = c["commit"]["message"].split("\n")[0]
                failures.append(f"[上游] {c['sha'][:9]} {message} — 评估移植并更新 pin")

    if failures:
        print("\n".join(failures), file=sys.stderr)
        return 1
    print(f"[通过] {len(local_files)} 件与 pin 一致，上游 {pin['branch']} 无路径新提交（@{pin['commit'][:9]}）")
    return 0


if __name__ == "__main__":
    sys.exit(main())
