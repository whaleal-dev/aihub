---
title: Tools 总览
description: aihub 只负责 tools / tool_calls 协议字段，以及把 @FunctionCall 编成 JSON Schema。
tags: [concept]
---

# Tools 总览

客户端做两件事：

- 把工具 schema 放进请求（`tools` / 部分厂商的 `functions`）
- 把模型返回的 `tool_calls` 解析成 Java 对象

可选：用 `@FunctionCall` 扫描本地方法，生成 JSON Schema，并在**你自己的代码里**按名字 invoke。这不是 MCP，也不是 Agent 工具执行器。

已删除的内建 coding tools、MCP gateway、BuiltInTools 不在本仓库。

## 继续阅读

- [Function Calling](/docs/capabilities/tools/function-calling)
- [注解式工具](/docs/capabilities/tools/annotation-based-tools)
- [第一次工具调用](/docs/getting-started/first-tool-call)
