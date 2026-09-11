---
sidebar_position: 998
title: 常见问题
description: aihub 是 Java 大模型客户端。这里回答第一次该看哪页、范围内有什么、范围内没有什么。
tags: [reference]
---

# 常见问题

## 1. 第一次接入该看哪页

1. [文档首页](/docs/intro)
2. [Java 快速开始](/docs/getting-started/quickstart-java)
3. 已有 Spring Boot 项目再看 [Spring Boot 快速开始](/docs/getting-started/quickstart-spring-boot)

公开站点：https://whaleal-dev.github.io/aihub/

## 2. aihub 是 SDK 还是 AI 基座

它是 **Java 大模型客户端**：发 HTTP / SSE 请求、解析响应。

范围内：Chat、Responses / Messages、Embedding、图像 / 音频 / 视频 / 音乐 / Realtime、Rerank、请求里的 `tools` 字段、Spring Boot Starter。

范围外：RAG、MCP、Agent 循环、工作流引擎、Coding CLI。这些请在业务侧自己做。

## 3. 请求里的 tools 会不会帮我执行工具

不会。客户端把 `tools` 编进请求，把 `tool_calls` 解析出来。执行函数、接 MCP、跑 Agent 循环都是调用方的事。见 [工具协议](/docs/capabilities/tools/overview)。

## 4. 要不要同时引很多模块

日常只引 `com.whaleal:aihub`。Spring 再加 `aihub-spring-boot-starter`。多模块时用 `aihub-bom` 对齐版本。

## 5. 文档和源码不一致怎么办

以仓库源码和 [README](https://github.com/whaleal-dev/aihub/blob/main/README.md) 为准，并提 Issue。
