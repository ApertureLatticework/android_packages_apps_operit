#!/usr/bin/env python3
"""Gradle 线 STT 模型预置：从 cnb 专仓拉字节，按清单校验后落位。

`syncSttModelAssets`（app/build.gradle.kts）的消费位是
`app/build/generated/stt-model-assets`；字节到位且过验（size+SHA256）后
Gradle 构建零网络。Gradle 任务本身仍是唯一校验裁判，本脚本只预置字节：

- 落位已完整过验 → 直接退出（CI 缓存命中路径，不克隆）
- 否则浅克隆 cnb 专仓，逐件对 `app/config/stt-model-assets.properties`
  （唯一事实源）校验，任何一件不符即失败退出——无兜底语义

Soong 树内线不经本脚本（local_manifests 挂 external/operit-ttsmodels，
Android.bp asset_dirs 直接消费）。

用法：
  python3 tools/dev/seed_stt_models.py                # 默认落位
  python3 tools/dev/seed_stt_models.py --dest <dir>   # 自定义落位
"""
import argparse
import hashlib
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
MANIFEST = REPO_ROOT / "app/config/stt-model-assets.properties"
DEFAULT_DEST = REPO_ROOT / "app/build/generated/stt-model-assets"
DEFAULT_REPO = "https://cnb.cool/SekaiMoeAOSPDev/android_external_operit_ttsmodels.git"
DEFAULT_BRANCH = "lineage-23.2"
# 专仓内模型根（asset_dirs 指向 assets，清单 target 自带 models/ 前缀）
CLONE_ASSET_ROOT = "assets"


def parse_manifest():
    assets = []
    for line in MANIFEST.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        target, _url, size, sha256, _desc, _license = line.split("|")
        assets.append((target, int(size), sha256))
    return assets


def verify(path: Path, size: int, sha256: str) -> bool:
    if not path.is_file() or path.stat().st_size != size:
        return False
    return hashlib.sha256(path.read_bytes()).hexdigest() == sha256


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dest", type=Path, default=DEFAULT_DEST)
    parser.add_argument("--repo", default=DEFAULT_REPO)
    parser.add_argument("--branch", default=DEFAULT_BRANCH)
    args = parser.parse_args()

    assets = parse_manifest()

    if all(verify(args.dest / target, size, sha256) for target, size, sha256 in assets):
        print(f"[跳过] {args.dest} 已完整过验（{len(assets)} 件）")
        return 0

    with tempfile.TemporaryDirectory(prefix="stt-seed-") as tmp_name:
        clone_root = Path(tmp_name) / "clone"
        print(f"[克隆] {args.repo}@{args.branch}")
        subprocess.run(
            ["git", "clone", "--quiet", "--depth", "1", "--branch", args.branch, args.repo, str(clone_root)],
            check=True,
        )

        # 共享完整性声明：专仓根清单副本必须与本仓清单逐字节一致
        remote_manifest = clone_root / MANIFEST.name
        if not remote_manifest.is_file() or remote_manifest.read_bytes() != MANIFEST.read_bytes():
            print("[专仓清单副本与本仓不一致] 两仓清单必须同 commit 更新", file=sys.stderr)
            return 1

        for target, size, sha256 in assets:
            source = clone_root / CLONE_ASSET_ROOT / target
            if not verify(source, size, sha256):
                print(f"[专仓件不符清单] {target}", file=sys.stderr)
                return 1

        for target, _size, _sha256 in assets:
            destination = args.dest / target
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(clone_root / CLONE_ASSET_ROOT / target, destination)

    bad = [t for t, s, h in assets if not verify(args.dest / t, s, h)]
    if bad:
        print(f"[落位校验失败] {bad}", file=sys.stderr)
        return 1
    print(f"[完成] {len(assets)} 件已落 {args.dest}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
