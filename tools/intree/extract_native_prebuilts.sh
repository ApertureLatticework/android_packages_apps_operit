#!/usr/bin/env bash
# 从 Gradle 构建产物抽取 native prebuilt 并落位（CI 的 android-build workflow 调用，亦可本地手跑）
# 用法：bash tools/intree/extract_native_prebuilts.sh [gradle-build-dir]
#   gradle-build-dir 默认 app/build；在 app/build 全树中定位 libsherpa-ncnn-jni.so
#   （优先 stripped → merged → 任意），按 abiFilters 实况仅 arm64-v8a。
# 落位后自动翻牌 native/sherpa-prebuilt/Android.bp.tree → Android.bp。
# ripgrep 的 .so 由 cargo 步骤直接产到 app/src/main/jniLibs/arm64-v8a/（prebuilt 本位），不经本脚本。
# 注意：本脚本 set -o pipefail，候选挑选一律用 grep -m1（自带停止），禁止 | head 管道
#（外层收银 head 提前关管会把上游 head 打成 Broken pipe，pipefail 下整步炸）。
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
BUILD="${1:-$ROOT/app/build}"
SHERPA_DST="$ROOT/native/sherpa-prebuilt"

all=$(find "$BUILD" -name "libsherpa-ncnn-jni.so" -path "*arm64-v8a*" 2>/dev/null || true)
[[ -z "$all" ]] && { echo "!! app/build 内未找到 arm64-v8a 的 libsherpa-ncnn-jni.so（先跑 Gradle 构建）" >&2; exit 1; }

# 优先级：stripped_native_libs > merged_native_libs > 任意首个
hit=$(grep -m1 "stripped_native_libs" <<<"$all" || true)
if [[ -z "$hit" ]]; then
    hit=$(grep -m1 "merged_native_libs" <<<"$all" || true)
fi
if [[ -z "$hit" ]]; then
    hit=$(grep -m1 "." <<<"$all" || true)
fi

mkdir -p "$SHERPA_DST/lib/arm64-v8a"
cp "$hit" "$SHERPA_DST/lib/arm64-v8a/"
echo "+ libsherpa-ncnn-jni.so (arm64-v8a) ← $hit"
sha256sum "$SHERPA_DST/lib/arm64-v8a/libsherpa-ncnn-jni.so"

# 翻牌：.tree → Android.bp（文件齐备，模块即刻自洽）
if [[ -f "$SHERPA_DST/Android.bp.tree" ]]; then
    mv "$SHERPA_DST/Android.bp.tree" "$SHERPA_DST/Android.bp"
    echo "+ native/sherpa-prebuilt/Android.bp 已翻牌"
fi
echo "完成。sherpa prebuilt 线闭合。"
