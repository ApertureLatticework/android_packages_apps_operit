from __future__ import annotations

import contextlib
import io
import sys
import tempfile
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(REPO_ROOT / "ci" / "script"))

from check_android_api_level import main  # noqa: E402


WORKFLOW_RELATIVE = (
    ".github/workflows/android-build.yml",
    ".github/workflows/android-tests.yml",
    ".github/workflows/pr-check.yml",
)
PS1_RELATIVE = "tools/native_ripgrep/build_native_ripgrep.ps1"

# 合成树只写断言真正消费的行；其余内容与裁决无关
WORKFLOW_TEMPLATE = "env:\n  ANDROID_API_LEVEL: '{level}'\n"
PS1_TEMPLATE = "param(\n    [int]$ApiLevel = {level}\n)\n"


def write_tree(root: Path, level: str = "31") -> None:
    for relative in WORKFLOW_RELATIVE:
        path = root / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(WORKFLOW_TEMPLATE.format(level=level), encoding="utf-8")
    ps1 = root / PS1_RELATIVE
    ps1.parent.mkdir(parents=True, exist_ok=True)
    ps1.write_text(PS1_TEMPLATE.format(level=level), encoding="utf-8")


def run_main(root: Path) -> tuple[int, str, str]:
    stdout, stderr = io.StringIO(), io.StringIO()
    with contextlib.redirect_stdout(stdout), contextlib.redirect_stderr(stderr):
        code = main(root)
    return code, stdout.getvalue(), stderr.getvalue()


class AndroidApiLevelConsistencyTest(unittest.TestCase):
    def test_consistent_declarations_pass(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            write_tree(root, level="31")

            code, stdout, _ = run_main(root)

            self.assertEqual(code, 0)
            self.assertIn("[通过]", stdout)
            self.assertIn("= 31", stdout)

    def test_missing_workflow_declaration_fails(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            write_tree(root, level="31")
            missing = root / WORKFLOW_RELATIVE[1]
            missing.write_text("env:\n  ANDROID_NDK_VERSION: '25.1.8937393'\n", encoding="utf-8")

            code, _, stderr = run_main(root)

            self.assertEqual(code, 1)
            self.assertIn(WORKFLOW_RELATIVE[1], stderr)
            self.assertIn("声明 0 处", stderr)

    def test_duplicate_declaration_fails(self) -> None:
        for relative, extra in (
            (WORKFLOW_RELATIVE[0], "\n  ANDROID_API_LEVEL: '31'\n"),
            (PS1_RELATIVE, "param(\n    [int]$ApiLevel = 31\n    [int]$ApiLevel = 31\n)\n"),
        ):
            with self.subTest(source=relative):
                with tempfile.TemporaryDirectory() as directory:
                    root = Path(directory)
                    write_tree(root, level="31")
                    if relative == PS1_RELATIVE:
                        (root / relative).write_text(extra, encoding="utf-8")
                    else:
                        with (root / relative).open("a", encoding="utf-8") as workflow:
                            workflow.write(extra)

                    code, _, stderr = run_main(root)

                    self.assertEqual(code, 1)
                    self.assertIn(relative, stderr)
                    self.assertIn("声明 2 处", stderr)

    def test_divergent_values_fail(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            write_tree(root, level="31")
            # 981c0a9 之前的真实形态：CI 改了、ps1 漏改
            (root / PS1_RELATIVE).write_text(PS1_TEMPLATE.format(level="23"), encoding="utf-8")

            code, _, stderr = run_main(root)

            self.assertEqual(code, 1)
            self.assertIn("不一致", stderr)
            for relative in (*WORKFLOW_RELATIVE, PS1_RELATIVE):
                self.assertIn(relative, stderr)
            self.assertIn("= 23", stderr)
            self.assertIn("= 31", stderr)

    def test_repository_tree_is_consistent(self) -> None:
        # 断言脚本只接线在 android-build（push main / 手动触发），PR 阶段靠
        # 本用例随快速车道把真树不变量钉进 unittest，漂移不再等到合并后才红
        code, stdout, _ = run_main(REPO_ROOT)

        self.assertEqual(code, 0)
        self.assertIn("[通过]", stdout)


if __name__ == "__main__":
    unittest.main()
