---
title: Spring Boot 总览
description: aihub-spring-boot-starter 把客户端注入 Spring，不重新定义模型语义。
tags: [integration]
---

# Spring Boot 总览

`aihub-spring-boot-starter` 用配置和 Bean 管理 aihub 客户端。它不是另一套 AI 实现。

> 在 Spring Boot 里注入 `AiService`、HTTP 客户端和各厂商 Config。不负责 RAG、MCP 或 Agent。

| 场景 | 是否适合 |
| --- | --- |
| 普通 Java `main` 先验证调用 | 不需要 starter |
| 已有 Spring Boot，要配置化接入 | 适合 |
| 需要 Bean 注入 `AiService` | 适合 |
| 需要多 provider | 适合 |

## 最小路径

1. [Spring Boot 快速开始](/docs/getting-started/quickstart-spring-boot)
2. [自动配置](/docs/integrations/spring-boot/auto-configuration)
3. [配置参考](/docs/integrations/spring-boot/configuration-reference)
