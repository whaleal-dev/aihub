---
title: 上线前检查
description: 使用 aihub 客户端上线前的检查项。
tags: [how-to]
---

# 上线前检查

- [ ] 依赖是 `com.whaleal:aihub`（或 starter），版本与 [Maven Central](https://search.maven.org/artifact/com.whaleal/aihub) 一致
- [ ] API Key 来自环境变量或密钥管理，未进 git
- [ ] `PlatformType` 与 Config / 模型名匹配
- [ ] 超时、代理、baseUrl 按环境区分
- [ ] 流式调用在连接断开时能结束监听
- [ ] 若使用 `tools` 字段：应用侧对 `tool_calls` 有白名单，客户端不会替你执行
