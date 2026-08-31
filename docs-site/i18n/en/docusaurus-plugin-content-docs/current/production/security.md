---
title: 安全说明
description: aihub 客户端使用时的密钥与 tool_calls 边界。
tags: [concept]
---

# 安全说明

- 密钥只走环境变量或密钥管理器。
- 不要把 Authorization 头打进日志。
- `tool_calls` 是模型输出，按不可信数据处理；本库只解析 JSON。
- 本仓库不含 MCP、RAG 入库或沙箱执行，那些边界在你的应用里。

披露流程见仓库根目录 [SECURITY.md](https://github.com/whaleal-dev/aihub/blob/main/SECURITY.md)。
