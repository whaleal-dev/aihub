---
title: 架构一览
description: aihub 客户端分层：配置、工厂、厂商适配、HTTP/SSE。
tags: [concept]
---

# 架构一览

```text
应用代码
  → Configuration + AiService
    → IChatService / IEmbeddingService / 媒体服务
      → platform.* 厂商适配
        → OkHttp + SSE 监听器
```

Spring Boot 只是把同一条链放进容器。没有独立的 Agent / RAG / MCP 层。
