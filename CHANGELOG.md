# Changelog

All notable changes to aihub are documented in this file. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Release notes are also published on the [GitHub Releases](https://github.com/whaleal-dev/aihub/releases) page.
Public docs: [GitHub Pages](https://whaleal-dev.github.io/aihub/).

## [1.0.0] — 2026-08-31

First release of **whaleal aihub** as a JDK 8+ Java LLM client.

### Added
- Core client (`io.github.whaleal-dev:aihub`): Chat / Completions (sync + SSE), Responses / Messages, Embedding, image / audio / video / music / Realtime, Rerank, multi-provider adapters, `tools` / `tool_calls` protocol fields.
- Spring Boot starter (`io.github.whaleal-dev:aihub-spring-boot-starter`).
- BOM (`io.github.whaleal-dev:aihub-bom`).
- Maven Central：`io.github.whaleal-dev` 坐标；推送 `release-*` 走 `-Pcentral` 自动发布（对齐 quick-sms）。

### Out of scope
- RAG, MCP, Agent runtime, workflow engines, Coding CLI, plugin host — not shipped in this tree.

The former 2.4.x line belonged to a multi-module Agent/RAG monorepo and is not this 1.0.0 version series.
