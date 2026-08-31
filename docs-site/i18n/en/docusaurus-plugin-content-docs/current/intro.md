---
sidebar_position: 1
title: aihub docs
description: aihub is a JDK 8+ Java LLM client. It wraps vendor APIs and does not ship RAG, MCP, or Agent.
---

# aihub docs

**aihub is a Java LLM client**, not an agent platform. It wraps vendor HTTP / SSE APIs behind one Java surface.

Public site: https://whaleal-dev.github.io/aihub/

## In scope

Chat (sync + streaming), Responses / Messages, Embedding, image / audio / video / music / Realtime, Rerank, multi-provider adapters, the `tools` request field, Spring Boot starter.

## Out of scope

RAG, MCP, Agent / A2A, workflow engines, Coding CLI, persistent memory, plugins and sandboxes. Build those in your application.

## Start here

| Goal | Doc |
|------|-----|
| First Chat call | [Java quickstart](/docs/getting-started/quickstart-java) |
| Spring Boot | [Spring Boot quickstart](/docs/getting-started/quickstart-spring-boot) |
| Model API conventions | [Model access](/docs/capabilities/models/overview) |
