---
title: 为什么选 aihub
description: aihub 是 JDK 8+ 的 Java 大模型客户端，只封装厂商 API。
tags: [concept]
---

# 为什么选 aihub

aihub 降低 Java 项目调用大模型的成本：少写各厂商 HTTP / SSE 胶水，同一套 `AiService` 切平台。

它不提供 RAG、MCP、Agent 运行时。那些应在业务侧实现。

## 解决什么问题

- 同时接 OpenAI 兼容网关和国内平台
- Chat、Responses、流式、多模态、Embedding、Rerank 的请求形态不一致
- 普通 Java 和 Spring Boot 希望同一套客户端，而不是两套 SDK

## 模块

| 阶段 | 模块 | 价值 |
| --- | --- | --- |
| 发出模型请求 | `aihub` | Chat / 媒体 / Embedding / Rerank |
| Spring 应用 | `aihub-spring-boot-starter` | 配置和 Bean 注入 |
| 版本对齐 | `aihub-bom` | 多模块同版本 |

## 和 Spring AI、LangChain4j

那些项目覆盖编排和 RAG。aihub 更窄：客户端把请求发出去、把响应解析回来。需要知识库或 Agent 循环时，用本客户端打模型，检索和循环写在你的应用里。

## 继续阅读

- [Java 快速开始](/docs/getting-started/quickstart-java)
- [模型接入](/docs/capabilities/models/overview)
