---
sidebar_position: 999
title: 术语表
description: aihub 客户端文档用到的术语。
tags: [reference]
---

# 术语表

### aihub

JDK 8+ Java 大模型客户端，Maven 坐标 `com.whaleal:aihub`。

### AiService

按 `Configuration` 与 `PlatformType` 创建 Chat / Embedding / 媒体等服务的工厂。

### Chat / Completions

各厂商对话补全 API 的客户端封装，含同步与 SSE。

### Responses / Messages

OpenAI Responses、Anthropic Messages 等另一套请求形态，仍是 HTTP 客户端，不是 Agent。

### Tool / Function Call

请求 JSON 里的 `tools` 与响应里的 `tool_calls`。客户端解析字段；是否执行工具由应用决定。

### Rerank

Jina / Ollama / Doubao 等重排序 HTTP 接口，不是完整 RAG。

### 范围外

RAG、MCP、Agent、Skill 资源、Coding CLI、FlowGram：本仓库不提供。
