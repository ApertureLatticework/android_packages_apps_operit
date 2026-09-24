# 3. 文档与八语字符串清除 [DONE]

## 旧实现情况

- feature-protocol 三文档：external_http_chat.md（229 行）、
  external_a2a_server.md、external_intent_chat.md
- dev-core 文档引用：BUILDING.md（Node 环境说明、构建步骤 5/6、故障表
  web-chat 行）、CONTRIBUTING.md（WebChat 环境与检查命令）、ci/README.md
  （lockfile 提及 web-chat）
- 字符串：external_http* 50 键，分布 values 与 en/es/id/ko/ms/pt-rBR/ro
  八个 locale（values-ja 本就无键）

## 意图修正

接口没了，协议文档与设置项文案一并消失，孤儿键零残留。

## 期待的新实现

三协议文档删除；dev-core 引用改写为无 web-chat 事实；八语 50 键清除。
