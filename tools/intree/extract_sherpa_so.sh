#!/usr/bin/env bash
# 从 Gradle 构建产物抽取 libsherpa-ncnn-jni.so 四 ABI 落位 native/sherpa-prebuilt/lib/
# 用法：bash tools/intree/extract_sherpa_so.sh <gradle 构建输出根>
#   <根> 指向 app/build/Intermediates 或任一含 jniLibs/<abi>/ 的目录；
#   也接受 APK 路径（内部 unzip 提取）。
# 落位后：mv native/sherpa-prebuilt/Android.bp.tree native/sherpa-prebuilt/Android.bp
set -euo pipefail

SRC="$1"
DST="$(cd "$(dirname "$0")/../.." && pwd)/native/sherpa-prebuilt"

find_one() { # 在目录树里找指定 ABI 的库
    find "$1" -path "*$2*" -name "libsherpa-ncnn-jni.so" -print -quit
}

mkdir -p "$DST/lib"
tmp=""
if [[ "$SRC" == *.apk ]]; then
    tmp=$(mktemp -d)
    unzip -q "$SRC" "lib/*" -d "$tmp"
    SRC="$tmp"
fi

declare -A MAP=( [arm]=armeabi-v7a [arm64-v8a]=arm64-v8a [x86]=x86 [x86_64]=x86_64 )
fail=0
for abi in "${!MAP[@]}"; do
    hit=$(find_one "$SRC" "${MAP[$abi]}")
    if [[ -z "$hit" ]]; then
        echo "!! ${MAP[$abi]} 未找到 libsherpa-ncnn-jni.so" >&2
        fail=1
        continue
    fi
    mkdir -p "$DST/lib/$abi"
    cp "$hit" "$DST/lib/$abi/"
    echo "+ ${MAP[$abi]} ← $hit"
    sha256sum "$DST/lib/$abi/libsherpa-ncnn-jni.so"
done

[[ -n "$tmp" ]] && rm -rf "$tmp"
if [[ $fail -eq 1 ]]; then
    echo "!! 有 ABI 缺失，勿翻牌" >&2
    exit 1
fi
echo "全部落位，可翻牌：mv native/sherpa-prebuilt/Android.bp.tree native/sherpa-prebuilt/Android.bp"
