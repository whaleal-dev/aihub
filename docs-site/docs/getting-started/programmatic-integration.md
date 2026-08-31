---
title: 编程式集成
description: 用 AiService 工厂把 aihub 嵌进普通 Java 或 Spring Boot 应用。
tags: [concept]
---

# 编程式集成

把 aihub 当库调用，不要找 CLI / ACP / Agent session。

## 普通 Java

```text
Configuration → AiService → IChatService / IEmbeddingService / …
```

见 [服务入口](/docs/capabilities/service-entry) 与 [Java 快速开始](/docs/getting-started/quickstart-java)。

## Spring Boot

引入 `aihub-spring-boot-starter`，用配置注入 Bean。见 [Spring Boot 快速开始](/docs/getting-started/quickstart-spring-boot)。

本仓库没有 ACP、CLI/TUI、trace replay 或 Agent 运行时。
