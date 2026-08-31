---
title: 包地图
description: aihub 模块里值得先看的包。
tags: [concept]
---

# 包地图

源码根：`aihub/src/main/java/com/whaleal/aihub/`

| 包簇 | 职责 |
| --- | --- |
| `service` / `service.factory` | `AiService`、平台枚举、各 I*Service |
| `platform` | 厂商请求/响应实体与 HTTP 调用 |
| `config` | 各厂商 Config |
| `network` | OkHttp、代理、超时 |
| `listener` | SSE |
| `tool` / `annotation` | Function Schema 与可选本地 invoke |
| `exception` | 错误映射 |
| `rerank` | 重排序实体 |

没有 `mcp`、`rag`、`vector`、`agentflow` 包。
