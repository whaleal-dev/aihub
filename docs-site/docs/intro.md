---
sidebar_position: 1
title: aihub 文档
description: aihub 是面向 JDK 8+ 的 Java 大模型客户端，只封装厂商 API，不做 RAG / MCP / Agent。
---

# aihub 文档

**aihub 是 Java 大模型客户端**，不是 Agent 平台。它把各厂商 HTTP / SSE API 收成同一套调用。

## 范围内

Chat（同步 + 流式）、Responses / Messages、Embedding、图像 / 音频 / 视频 / 音乐 / Realtime、Rerank、多厂商适配、请求里的 `tools` 字段、Spring Boot Starter。

## 范围外

RAG、MCP、Agent / A2A、工作流引擎、Coding CLI、会话记忆、插件与沙箱。这些应在业务侧自行实现。

## 入口

| 目标 | 文档 |
|------|------|
| 发出第一条 Chat | [Java 快速开始](/docs/getting-started/quickstart-java) |
| Spring Boot 接入 | [Spring Boot 快速开始](/docs/getting-started/quickstart-spring-boot) |
| 模型 API 约定 | [模型接入](/docs/capabilities/models/overview) |
