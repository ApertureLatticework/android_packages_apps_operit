# 3. 一致性门禁与配套完善 [DONE]

## 旧实现情况

四处声明的一致性无任何门禁：981c0a9 修的 26/23 错配靠人手扫出；即便有了断言脚本，
它也只接线在 android-build（push main / 手动触发），PR 阶段的漂移要等合并后才红。
ps1 的 `$targetConfig` 仍挂四 ABI 条目，「单 ABI 有意」声明只存在于 workflow 注释里。

## 意图修正

- 「改一处漏三处」变成 CI 即红：声明次数（恰好一次）+ 取值（四处同值）双重断言
- 门禁前置到 PR 阶段：断言逻辑固化为标准库单测，随快速车道全量跑
- ps1 ABI 面与实际分发面一致，意图声明下沉到 ps1 本体

## 新实现情况

- `ci/script/check_android_api_level.py`（a59687a）：每份 workflow 必须恰好声明一次
  `ANDROID_API_LEVEL`（漏/重复都红）；ps1 必须恰好声明一次 `[int]$ApiLevel` 默认值；
  四处取值必须相等。取值本身不归脚本管（产品决策）。`main(repo_root)` 可注入合成树
- `ci/test/test_android_api_level.py`：4 场景固化——一致绿 / workflow 漏声明红 /
  声明重复红（workflow、ps1 双子测）/ 取值不一致红；另有真树不变量用例，把
  「当前仓四处=31」钉进 `python3 -B -m unittest discover -s ci/test`（pr-check
  快速车道，所有改动全量跑）
- ps1 `$targetConfig` 收敛到单 aarch64 条目（a59687a 后配套完善）：注释声明
  「单 ABI 有意、与 workflow 预检步同源」，扩 ABI 时补条目并同步 CI 预检清单；
  旧四条目见 git 历史

## 验证

- 全量 `python3 -B -m unittest discover -s ci/test`：52 用例 OK
- 双向实测：真树 `check_android_api_level.py` exit 0；临时把 pr-check.yml 改 `'26'`
  后单测红（`AssertionError: 1 != 0`）、脚本 exit 1 并列出四处取值；还原后复绿
