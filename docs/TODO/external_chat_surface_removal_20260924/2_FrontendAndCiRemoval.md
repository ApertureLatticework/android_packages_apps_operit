# 2. 前端工程与 CI 清理 [DONE]

## 旧实现情况

- `web-chat/`：React 19 + Vite 工程（含 scripts/sync-to-android-assets.mjs，
  产物同步 `app/src/main/assets/web-chat/`，gitignore）
- 根 package.json：`build:webchat` 脚本
- .gitignore 72-74：web-chat/dist 与 assets/web-chat 两条目及注释
- android-build.yml：paths 过滤 web-chat/**、Install JS deps 中
  `npm --prefix web-chat ci`、Build WebChat assets 步骤
- pr-check.yml：plan 门控 web 输出、typecheck、build:webchat
- android-tests.yml：paths 过滤 web-chat/**

## 意图修正

前端门面不复存在，CI 不再需要 Node web 线。

## 期待的新实现

工程整删；根 package.json 删 build:webchat（其余 npm 线保留）；
CI 三 yml 摘除 web-chat 相关步骤与过滤；.gitignore 条目删除。
