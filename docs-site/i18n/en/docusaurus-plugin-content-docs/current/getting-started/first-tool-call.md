---
title: 第一次工具调用
description: 客户端把 tools 编进请求、解析 tool_calls。执行工具由调用方完成。
tags: [concept]
---

# 第一次工具调用

aihub 处理的是 **协议字段**，不是工具运行时。

1. 用 `@FunctionCall` 或手写 schema 得到 `tools`
2. 放进 `ChatCompletion` / `ResponseRequest`
3. 从响应里读 `tool_calls`
4. **你的代码**执行函数，再把结果当 `tool` 消息发回去

本仓库不提供 MCP、Skill 文件加载或 Agent 循环。

最短示例见 [Function Calling](/docs/capabilities/tools/function-calling) 与 [注解式工具](/docs/capabilities/tools/annotation-based-tools)。
