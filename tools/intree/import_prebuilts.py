#!/usr/bin/env python3
"""Operit 树内 prebuilt 接线器（步骤 5 依赖策略修订，2026-09-20）。

职责：把 Gradle 侧非 androidx 依赖以 prebuilt 形态接入树内构建——
  1. 从根坐标（与 app/build.gradle.kts 对齐）解析 POM 传递闭包
  2. maven central / google maven / jitpack 三仓路由下载 AAR/JAR
  3. 每库生成 libs/<module>/{工件, Android.bp, NOTICE}，全部落 libs/prebuilts.lock（sha256）

用法：
  python3 tools/intree/import_prebuilts.py --dry-run   # 只解析打印闭包表
  python3 tools/intree/import_prebuilts.py --fetch     # 下载并生成全部文件
  python3 tools/intree/import_prebuilts.py --verify    # 校验 lock 与本地文件 sha256

约束：
  - 树内已有模块供应的组不进闭包（androidx / kotlin / kotlinx-coroutines / serialization /
    material），避免与 prebuilts/sdk 及 external/kotlin* 打架
  - ffmpeg 线例外：ffmpeg-kit AAR 与 smart-exception 不在根坐标内（走 external/ffmpeg
    源码线 + liboperit_ffmpeg 薄壳）
  - 本脚本只做机械搬运，不引入任何源码
"""

import argparse
import hashlib
import json
import re
import sys
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent.parent
LIBS_DIR = REPO_ROOT / "libs"
LOCK_FILE = LIBS_DIR / "prebuilts.lock"

REPOSITORIES = [
    ("central", "https://repo1.maven.org/maven2/"),
    ("google", "https://dl.google.com/android/maven2/"),
    ("jitpack", "https://jitpack.io/"),
]

POM_NS = {"m": "http://maven.apache.org/POM/4.0.0"}

# 根坐标：(group, artifact, version, module)——module 与 Android.bp.tree 的 static_libs 对齐
ROOTS = [
    # 网络
    ("com.squareup.okhttp3", "okhttp", "4.12.0", "operit-okhttp"),
    ("com.squareup.okhttp3", "okhttp-sse", "4.12.0", "operit-okhttp-sse"),
    ("com.squareup.okhttp3", "logging-interceptor", "4.12.0", "operit-okhttp-logging-interceptor"),
    ("io.ktor", "ktor-client-okhttp", "3.2.3", "operit-ktor-client-okhttp"),
    ("io.modelcontextprotocol", "kotlin-sdk-client", "0.10.0", "operit-mcp-sdk-client"),
    ("org.jsoup", "jsoup", "1.16.2", "operit-jsoup"),
    # Agent 工具面
    ("com.google.zxing", "core", "3.5.3", "operit-zxing-core"),
    ("io.github.java-diff-utils", "java-diff-utils", "4.12", "operit-java-diff-utils"),
    ("com.android.tools.build", "apksig", "8.1.0", "operit-apksig"),
    ("net.dongliu", "apk-parser", "2.6.10", "operit-apk-parser"),
    ("com.github.Sable", "axml", "2.0.0", "operit-axml"),
    ("com.github.iyxan23", "zipalign-java", "1.2.1", "operit-zipalign-java"),
    ("org.apache.commons", "commons-compress", "1.25.0", "operit-commons-compress"),
    ("commons-io", "commons-io", "2.13.0", "operit-commons-io"),
    ("net.lingala.zip4j", "zip4j", "2.11.5", "operit-zip4j"),
    ("com.github.junrar", "junrar", "7.5.5", "operit-junrar"),
    # 图片与显示
    ("io.coil-kt", "coil", "2.5.0", "operit-coil"),
    ("io.coil-kt", "coil-compose", "2.5.0", "operit-coil-compose"),
    ("io.coil-kt", "coil-gif", "2.5.0", "operit-coil-gif"),
    ("com.caverock", "androidsvg-aar", "1.4", "operit-androidsvg"),
    ("pl.droidsonroids.gif", "android-gif-drawable", "1.2.28", "operit-android-gif"),
    ("com.vanniktech", "android-image-cropper", "4.5.0", "operit-image-cropper"),
    ("ru.noties", "jlatexmath-android", "0.2.0", "operit-jlatexmath"),
    ("com.github.tech-pw", "RenderX", "1.0.0", "operit-renderx"),
    # 文档线
    ("com.itextpdf", "itextg", "5.5.10", "operit-itextg"),
    ("com.tom-roush", "pdfbox-android", "2.0.27.0", "operit-pdfbox-android"),
    ("org.apache.poi", "poi", "5.2.3", "operit-poi"),
    ("org.apache.poi", "poi-ooxml", "5.2.3", "operit-poi-ooxml"),
    ("org.apache.poi", "poi-scratchpad", "5.2.3", "operit-poi-scratchpad"),
    # 数据与杂项
    ("com.google.code.gson", "gson", "2.10.1", "operit-gson"),
    ("org.hjson", "hjson", "3.0.0", "operit-hjson"),
    ("com.benasher44", "uuid", "0.8.2", "operit-uuid"),
    ("com.huaban", "jieba-analysis", "1.0.2", "operit-jieba"),
    ("com.github.jelmerk", "hnswlib-core", "0.0.46", "operit-hnswlib-core"),
    ("com.github.jelmerk", "hnswlib-utils", "0.0.46", "operit-hnswlib-utils"),
    ("org.bouncycastle", "bcprov-jdk18on", "1.78", "operit-bcprov"),
    ("org.nanohttpd", "nanohttpd", "2.3.1", "operit-nanohttpd"),
    # UI 组件
    ("com.github.skydoves", "colorpicker-compose", "1.0.6", "operit-colorpicker"),
    ("io.github.kyant0", "backdrop", "1.0.6", "operit-backdrop"),
    ("io.github.fletchmckee.liquid", "liquid", "1.1.1", "operit-liquid"),
    ("sh.calvin.reorderable", "reorderable", "2.5.1", "operit-reorderable"),
    ("me.saket.swipe", "swipe", "1.2.0", "operit-swipe"),
    ("com.google.accompanist", "accompanist-systemuicontroller", "0.32.0", "operit-accompanist-systemuicontroller"),
    # 记忆/向量推理线（AAR 内含 native）
    ("org.tensorflow", "tensorflow-lite", "2.10.0", "operit-tensorflow-lite"),
    ("com.google.mediapipe", "tasks-text", "0.10.11", "operit-mediapipe-tasks-text"),
    ("com.microsoft.onnxruntime", "onnxruntime-android", "1.17.1", "operit-onnxruntime"),
    # ML Kit（prebuilt 默认策略下与其他坐标同机制）
    ("com.google.mlkit", "text-recognition", "16.0.0", "operit-mlkit-text-recognition"),
    ("com.google.mlkit", "text-recognition-chinese", "16.0.0", "operit-mlkit-text-recognition-chinese"),
    ("com.google.mlkit", "text-recognition-japanese", "16.0.0", "operit-mlkit-text-recognition-japanese"),
    ("com.google.mlkit", "text-recognition-korean", "16.0.0", "operit-mlkit-text-recognition-korean"),
    ("com.google.mlkit", "text-recognition-devanagari", "16.0.0", "operit-mlkit-text-recognition-devanagari"),
]

# 传递闭包跳过规则：树内已有供应或属 Gradle 构建期概念。
# 注意：KMP 多平台变体不靠硬裁剪，由 .module 元数据的变体选择消解。
SKIP_GROUP_PREFIXES = (
    "androidx.",
    "com.google.android.material",
    "org.jetbrains.kotlin",
    "org.jetbrains.compose.",
    "org.jetbrains.androidx.",
    "org.jetbrains.skiko",
    "org.junit",
    "junit",
    "org.robolectric",
)
SKIP_EXACT = {
    "com.android.tools.build:gradle",
    "com.android.tools:sdk-common",
}
SKIP_ARTIFACT_PREFIXES = {
    "org.jetbrains.kotlinx:kotlinx-coroutines-",
    "org.jetbrains.kotlinx:kotlinx-serialization-",
}

# .module 变体选择优先级：Android 目标优先，无 android 变体时退 jvm（ktor 等纯 jvm 库）
PLATFORM_PREFERENCE = ("androidTarget", "androidJvm", "jvm")


@dataclass
class Artifact:
    group: str
    artifact: str
    version: str
    module: str
    packaging: str = "jar"
    pom_repo: str = "?"
    licenses: list = None
    sha256: str = ""
    filename: str = ""
    size: int = 0
    origin: str = "root"

    def __post_init__(self):
        if self.licenses is None:
            self.licenses = []

    @property
    def coord(self) -> str:
        return f"{self.group}:{self.artifact}:{self.version}"


CACHE_DIR = Path("/tmp/operit-prebuilt-cache")


def http_get(url: str) -> bytes | None:
    cache_key = CACHE_DIR / (hashlib.sha256(url.encode()).hexdigest() + ".bin")
    marker = CACHE_DIR / (hashlib.sha256(url.encode()).hexdigest() + ".miss")
    if cache_key.is_file():
        return cache_key.read_bytes()
    if marker.is_file():
        return None

    request = urllib.request.Request(url, headers={"User-Agent": "operit-intree-import/1.0"})
    for attempt in range(2):
        try:
            with urllib.request.urlopen(request, timeout=60) as response:
                data = response.read()
                CACHE_DIR.mkdir(exist_ok=True)
                cache_key.write_bytes(data)
                return data
        except urllib.error.HTTPError as e:
            if e.code == 404:
                CACHE_DIR.mkdir(exist_ok=True)
                marker.write_text("404", encoding="utf-8")
                return None
            print(f"  ! HTTP {e.code} {url}", file=sys.stderr)
        except (urllib.error.URLError, TimeoutError) as e:
            print(f"  ! network {e} {url} (attempt {attempt + 1})", file=sys.stderr)
    return None


def locate(g: str, a: str, v: str, filename: str) -> tuple[str, bytes] | None:
    for repo_name, base in REPOSITORIES:
        url = f"{base}{g.replace('.', '/')}/{a}/{v}/{filename}"
        data = http_get(url)
        if data is not None:
            return repo_name, data
    return None


def parse_pom(pom: bytes) -> tuple[str, list[tuple[str, str, str]], list[tuple[str, str]]]:
    root = ET.fromstring(pom)
    packaging_el = root.find("m:packaging", POM_NS)
    packaging = packaging_el.text.strip() if packaging_el is not None else "jar"
    if packaging == "bundle":
        # maven-bundle 打包在仓库里以普通 jar 形态存在（guava/protobuf-javalite）
        packaging = "jar"

    deps: list[tuple[str, str, str]] = []
    skipped_optional = 0
    for dep in root.findall("m:dependencies/m:dependency", POM_NS):
        g_el = dep.find("m:groupId", POM_NS)
        a_el = dep.find("m:artifactId", POM_NS)
        v_el = dep.find("m:version", POM_NS)
        s_el = dep.find("m:scope", POM_NS)
        o_el = dep.find("m:optional", POM_NS)
        if None in (g_el, a_el, v_el):
            continue
        scope = s_el.text.strip() if s_el is not None else "compile"
        optional = (o_el is not None and o_el.text.strip() == "true")
        if scope in ("test", "provided", "system", "import") or optional:
            skipped_optional += 1
            continue
        version = v_el.text.strip()
        # 属性引用/区间交由根坐标或人工补录
        if version.startswith("${") or version.startswith("["):
            continue
        deps.append((g_el.text.strip(), a_el.text.strip(), version))

    licenses: list[tuple[str, str]] = []
    for lic in root.findall("m:licenses/m:license", POM_NS):
        name_el = lic.find("m:name", POM_NS)
        url_el = lic.find("m:url", POM_NS)
        licenses.append(
            (
                name_el.text.strip() if name_el is not None else "UNKNOWN",
                url_el.text.strip() if url_el is not None else "",
            )
        )
    return packaging, deps, licenses


def should_skip(g: str, a: str) -> bool:
    coord = f"{g}:{a}"
    if coord in SKIP_EXACT:
        return True
    if any(g.startswith(p) for p in SKIP_GROUP_PREFIXES):
        return True
    if any(coord.startswith(p) for p in SKIP_ARTIFACT_PREFIXES):
        return True
    return False


def module_for(g: str, a: str) -> str:
    return "operit-" + re.sub(r"[^a-z0-9]+", "-", a.lower()).strip("-")


def parse_gradle_module(doc: dict) -> dict | None:
    """解析 .module 元数据：按 KMP 平台属性选变体，变体在别模块时重定向。

    返回 {redirect:(g,a,v)} 或 {packaging, deps:[(g,a,v)]}。"""
    variants = doc.get("variants", [])

    def platform_of(variant: dict) -> str:
        return str(variant.get("attributes", {}).get("org.jetbrains.kotlin.platform.type", ""))

    chosen = None
    for want in PLATFORM_PREFERENCE:
        candidates = [v for v in variants if platform_of(v) == want]
        if not candidates:
            continue
        # AGP 发布的库同平台多 buildType 变体，只取 release
        def is_release(variant: dict) -> bool:
            attrs = variant.get("attributes", {})
            build_type = attrs.get("com.android.build.api.attributes.BuildTypeAttr")
            if build_type is None:
                build_type = attrs.get("org.jetbrains.kotlinx.kotlin.buildtype")
            return build_type in (None, "release")

        release_candidates = [v for v in candidates if is_release(v)]
        chosen = (release_candidates or candidates)[0]
        break
    if chosen is None:
        chosen = next((v for v in variants if v.get("files")), None)
    if chosen is None:
        return None

    available_at = chosen.get("available-at")
    if available_at:
        return {
            "redirect": (
                available_at["group"],
                available_at["module"],
                available_at.get("version", ""),
            )
        }

    packaging = "jar"
    for f in chosen.get("files", []):
        if str(f.get("url", "")).endswith(".aar"):
            packaging = "aar"
            break

    deps: list[tuple[str, str, str]] = []
    for dep in chosen.get("dependencies", []):
        version = dep.get("version", {})
        requires = version.get("requires") if isinstance(version, dict) else None
        if not requires:
            continue
        deps.append((dep["group"], dep["module"], str(requires)))
    return {"packaging": packaging, "deps": deps}


def resolve_coordinate(g: str, a: str, v: str):
    """单坐标解析：.module 元数据优先（KMP 变体语义），POM 兜底普通库。
    返回 ("redirect", (g,a,v)) / (packaging, deps) / None。"""
    module_file = f"{a}-{v}.module"
    for _repo_name, base in REPOSITORIES:
        data = http_get(f"{base}{g.replace('.', '/')}/{a}/{v}/{module_file}")
        if data is None:
            continue
        parsed = parse_gradle_module(json.loads(data))
        if parsed is None:
            break
        if "redirect" in parsed:
            return "redirect", parsed["redirect"]
        return parsed["packaging"], parsed["deps"]

    pom_file = f"{a}-{v}.pom"
    for _repo_name, base in REPOSITORIES:
        data = http_get(f"{base}{g.replace('.', '/')}/{a}/{v}/{pom_file}")
        if data is not None:
            packaging, deps, _licenses = parse_pom(data)
            return packaging, deps
    return None


def resolve() -> list[Artifact]:
    seen: dict[str, Artifact] = {}
    queue: list[Artifact] = []
    for g, a, v, module in ROOTS:
        artifact = Artifact(g, a, v, module)
        seen[f"{g}:{a}"] = artifact
        queue.append(artifact)

    unresolved: list[str] = []
    while queue:
        current = queue.pop(0)
        result = resolve_coordinate(current.group, current.artifact, current.version)
        if result is None:
            unresolved.append(current.coord)
            continue
        if result[0] == "redirect":
            rg, ra, rv = result[1]
            key = f"{rg}:{ra}"
            if key not in seen:
                # KMP 根壳 → 平台变体：沿用根的模块名，根壳本身不入库
                target = Artifact(rg, ra, rv or current.version, current.module, origin=current.origin)
                seen[key] = target
                queue.append(target)
            seen.pop(f"{current.group}:{current.artifact}", None)
            continue
        packaging, deps = result
        current.packaging = packaging
        for g, a, v in deps:
            key = f"{g}:{a}"
            if key in seen or should_skip(g, a):
                continue
            child = Artifact(g, a, v, module_for(g, a), origin="transitive")
            seen[key] = child
            queue.append(child)

    if unresolved:
        print("!! 不可达坐标（人工核对版本或 jitpack 构建状态）：", file=sys.stderr)
        for coord in unresolved:
            print(f"   {coord}", file=sys.stderr)
    return sorted(seen.values(), key=lambda x: x.module)


def pick_file(artifact: Artifact) -> tuple[str, str, bytes] | None:
    if artifact.packaging == "aar":
        preferred = [".aar", ".jar"]
    else:
        preferred = [".jar", ".aar"]
    for ext in preferred:
        filename = f"{artifact.artifact}-{artifact.version}{ext}"
        found = locate(artifact.group, artifact.artifact, artifact.version, filename)
        if found is not None:
            return found[0], filename, found[1]
    return None


def render_android_bp(artifact: Artifact) -> str:
    header = (
        f"// {artifact.coord} · {artifact.pom_repo} prebuilt\n"
        f"// 由 tools/intree/import_prebuilts.py 生成，升级请改脚本根坐标后重跑\n"
    )
    if artifact.packaging == "aar":
        return (
            header
            + "android_library_import {\n"
            f'    name: "{artifact.module}",\n'
            f'    aars: ["{artifact.filename}"],\n'
            '    visibility: ["//packages/apps/Operit:__subpackages__"],\n'
            "}\n"
        )
    return (
        header
        + "java_import {\n"
        f'    name: "{artifact.module}",\n'
        f'    jars: ["{artifact.filename}"],\n'
        '    visibility: ["//packages/apps/Operit:__subpackages__"],\n'
        "}\n"
    )


def render_notice(artifact: Artifact) -> str:
    lines = [f"{artifact.coord} ({artifact.pom_repo})", ""]
    if artifact.licenses:
        for name, url in artifact.licenses:
            lines.append(f"License: {name}" + (f" <{url}>" if url else ""))
    else:
        lines.append("License: 未在 POM 声明，随源上游")
    lines.append("")
    lines.append("Prebuilt artifact imported verbatim; no source modifications.")
    return "\n".join(lines)


def cmd_dry_run(artifacts: list[Artifact]) -> None:
    print(f"{'module':44s} {'coord':58s} {'pkg':4s} {'origin':11s}")
    total = 0
    for a in artifacts:
        print(f"{a.module:44s} {a.coord:58s} {a.packaging:4s} {a.origin:11s}")
        total += 1
    print(f"-- 共 {total} 项（根 {len(ROOTS)}，传递 {total - len(ROOTS)}）")


def cmd_fetch(artifacts: list[Artifact]) -> None:
    LIBS_DIR.mkdir(exist_ok=True)
    lock_lines: list[str] = []
    for a in artifacts:
        found = pick_file(a)
        if found is None:
            print(f"!! 工件下载失败，跳过：{a.coord}", file=sys.stderr)
            continue
        a.pom_repo, a.filename, data = found
        a.size = len(data)
        a.sha256 = hashlib.sha256(data).hexdigest()

        pom = locate(a.group, a.artifact, a.version, f"{a.artifact}-{a.version}.pom")
        if pom is not None:
            _pkg, _deps, a.licenses = parse_pom(pom[1])

        target_dir = LIBS_DIR / a.module
        target_dir.mkdir(exist_ok=True)
        (target_dir / a.filename).write_bytes(data)
        (target_dir / "Android.bp").write_text(render_android_bp(a), encoding="utf-8")
        (target_dir / "NOTICE").write_text(render_notice(a), encoding="utf-8")
        lock_lines.append(f"{a.sha256}  {a.module}  {a.coord}  {a.filename}  {a.size}")
        print(f"+ {a.module:44s} {a.size:>10,d} B  {a.filename}")

    LOCK_FILE.write_text("\n".join(lock_lines) + "\n", encoding="utf-8")
    total = sum(a.size for a in artifacts if a.sha256)
    print(f"-- 共 {len(lock_lines)} 项，{total / 1024 / 1024:.1f} MB，lock 落于 {LOCK_FILE.relative_to(REPO_ROOT)}")


def cmd_verify() -> None:
    if not LOCK_FILE.exists():
        sys.exit("lock 不存在，先跑 --fetch")
    failures = 0
    for line in LOCK_FILE.read_text(encoding="utf-8").splitlines():
        if not line.strip():
            continue
        sha, module, coord, filename, _size = re.split(r"\s{2,}", line.strip())
        path = LIBS_DIR / module / filename
        if not path.is_file():
            print(f"!! 缺文件 {path.relative_to(REPO_ROOT)}")
            failures += 1
            continue
        actual = hashlib.sha256(path.read_bytes()).hexdigest()
        if actual != sha:
            print(f"!! sha256 不符 {path.relative_to(REPO_ROOT)}")
            failures += 1
    if failures:
        sys.exit(f"校验失败 {failures} 项")
    print("lock 校验全部通过")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--dry-run", action="store_true", help="只解析并打印闭包")
    group.add_argument("--fetch", action="store_true", help="下载并生成 libs/ 全套文件")
    group.add_argument("--verify", action="store_true", help="校验 lock 与本地文件")
    args = parser.parse_args()

    if args.verify:
        cmd_verify()
        return
    artifacts = resolve()
    cmd_dry_run(artifacts)
    if args.fetch:
        cmd_fetch(artifacts)


if __name__ == "__main__":
    main()
