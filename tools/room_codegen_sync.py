#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Room 生成代码 check-in 同步工具（步骤3机制）

Room 生成类已 check-in 进 app/src/main/java，默认 Gradle 构建零注解处理器
（app/build.gradle.kts 仅在 -ProomRegen 时注入 room-compiler）。

用法：
    ./gradlew :app:kaptDebugKotlin -ProomRegen --no-daemon
    python3 tools/room_codegen_sync.py            # 同步生成类到 src/main/java
    python3 tools/room_codegen_sync.py --check    # CI 一致性检查：有漂移则退出码 1
    python3 tools/room_codegen_sync.py --prune    # 同步并删除孤儿生成类

schema 变更流程：改实体 → 上述再生成 → 连同生成类一并提交。
"""
import argparse
import filecmp
import shutil
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
GENERATED_GLOB = "generated/source/kapt*"
VARIANT = "debug"
DEST_ROOT = REPO_ROOT / "app/src/main/java"


def find_generated_root(from_dir: str | None) -> Path:
    if from_dir:
        root = Path(from_dir).resolve()
        if not root.is_dir():
            sys.exit(f"--from 目录不存在: {root}")
        return root
    candidates = sorted(
        p for p in REPO_ROOT.glob(f"app/build/{GENERATED_GLOB}/{VARIANT}") if p.is_dir()
    )
    if not candidates:
        sys.exit(f"未找到 kapt 生成目录（先跑 ./gradlew :app:kapt{VARIANT.capitalize()}Kotlin -ProomRegen，或用 --from 指定 CI artifact 解压目录）")
    return candidates[-1]


def collect_files(root: Path) -> dict[str, Path]:
    return {
        str(p.relative_to(root)): p
        for p in root.rglob("*.java")
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="仅对比，存在漂移以退出码 1 失败")
    parser.add_argument("--prune", action="store_true", help="同步时删除 check-in 中的孤儿生成类")
    parser.add_argument("--from", dest="from_dir", default=None, help="生成源目录（默认 app/build kapt 输出；可指 CI artifact 解压目录）")
    args = parser.parse_args()

    generated = collect_files(find_generated_root(args.from_dir))
    if not generated:
        sys.exit("kapt 生成目录为空：Room compiler 未生效（确认 -ProomRegen 已传入）")

    added, changed, same = [], [], []
    for rel, src in sorted(generated.items()):
        dest = DEST_ROOT / rel
        if not dest.exists():
            added.append(rel)
        elif not filecmp.cmp(src, dest, shallow=False):
            changed.append(rel)
        else:
            same.append(rel)

    orphans = sorted(
        str(p.relative_to(DEST_ROOT))
        for p in DEST_ROOT.rglob("*_Impl.java")
        if str(p.relative_to(DEST_ROOT)) not in generated
    )

    drift = bool(added or changed or orphans)
    for rel in added:
        print(f"新增: {rel}")
    for rel in changed:
        print(f"变更: {rel}")
    for rel in orphans:
        print(f"孤儿: {rel}")
    print(f"一致: {len(same)} 个文件")

    if args.check:
        if drift:
            print("生成类与 check-in 存在漂移：请本地再生成并提交（tools/room_codegen_sync.py）")
            return 1
        print("生成类一致性检查通过")
        return 0

    for rel in added + changed:
        dest = DEST_ROOT / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(generated[rel], dest)
    if args.prune:
        for rel in orphans:
            (DEST_ROOT / rel).unlink()
    print(f"同步完成: 新增 {len(added)}，变更 {len(changed)}，删除孤儿 {len(orphans) if args.prune else 0}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
