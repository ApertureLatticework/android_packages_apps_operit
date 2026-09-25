#!/usr/bin/env python3
"""树内 STT 模型资产同步（Gradle syncSttModelAssets 的 Soong 侧镜像）。

读取 app/config/stt-model-assets.properties（与 Gradle 线同一份清单与校验），
下载/校验到 app/src/main/assets/models/（gitignore 路径，Soong asset_dirs 自动拾取）。

用法：
  python3 tools/intree/sync_stt_models.py           # 下载/校验缺失件
  python3 tools/intree/sync_stt_models.py --check   # 仅校验，缺件报错退出
  HF_ENDPOINT=https://hf-mirror.com python3 ...     # 显式换源
"""
import argparse
import hashlib
import os
import sys
import tempfile
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
MANIFEST = REPO_ROOT / "app/config/stt-model-assets.properties"
TARGET_ROOT = REPO_ROOT / "app/src/main/assets"


def parse_manifest():
    assets = []
    for line in MANIFEST.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        target, url, size, sha256, _desc, _license = line.split("|")
        assets.append((target, url, int(size), sha256))
    return assets


def verify(path: Path, size: int, sha256: str) -> bool:
    if not path.is_file() or path.stat().st_size != size:
        return False
    digest = hashlib.sha256(path.read_bytes()).hexdigest()
    return digest == sha256


def download(url: str, destination: Path) -> None:
    destination.parent.mkdir(parents=True, exist_ok=True)
    fd, tmp_name = tempfile.mkstemp(dir=destination.parent)
    tmp_path = Path(tmp_name)
    os.close(fd)
    try:
        with urllib.request.urlopen(url, timeout=120) as response, tmp_path.open("wb") as out:
            total = int(response.headers.get("Content-Length", 0))
            done = 0
            while True:
                chunk = response.read(1 << 20)
                if not chunk:
                    break
                out.write(chunk)
                done += len(chunk)
                if total:
                    print(f"\r  {done}/{total} bytes", end="", flush=True)
        print()
        tmp_path.replace(destination)
    finally:
        tmp_path.unlink(missing_ok=True)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true", help="仅校验，缺件即失败")
    args = parser.parse_args()

    hf_endpoint = os.environ.get("HF_ENDPOINT", "").rstrip("/")
    assets = parse_manifest()
    failures = []
    for target, url, size, sha256 in assets:
        destination = (TARGET_ROOT / target).resolve()
        if not str(destination).startswith(str(TARGET_ROOT.resolve())):
            print(f"[路径逃逸] {target}", file=sys.stderr)
            return 1
        if verify(destination, size, sha256):
            print(f"[OK] {target}")
            continue
        if args.check:
            print(f"[缺失] {target}", file=sys.stderr)
            failures.append(target)
            continue
        print(f"[下载] {target}")
        effective_url = url.replace("https://huggingface.co", hf_endpoint, 1) if hf_endpoint else url
        download(effective_url, destination)
        if not verify(destination, size, sha256):
            print(f"[校验失败] {target}", file=sys.stderr)
            return 1
        print(f"[完成] {target}")
    if failures:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
