# Changelog

All notable changes to aihub are documented in this file. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Release notes are also published on the [GitHub Releases](https://github.com/whaleal-dev/aihub/releases) page.
Public docs: [GitHub Pages](https://whaleal-dev.github.io/aihub/).

## [1.0.0] — 2026-08-31

First release of **whaleal aihub** as a JDK 8+ Java LLM client.

### Added
- Core client (`com.whaleal:aihub`): Chat / Completions (sync + SSE), Responses / Messages, Embedding, image / audio / video / music / Realtime, Rerank, multi-provider adapters, `tools` / `tool_calls` protocol fields.
- Spring Boot starter (`com.whaleal:aihub-spring-boot-starter`).
- BOM (`com.whaleal:aihub-bom`).

### Out of scope
- RAG, MCP, Agent runtime, workflow engines, Coding CLI, plugin host — not shipped in this tree.

The former 2.4.x line belonged to a multi-module Agent/RAG monorepo and is not this 1.0.0 version series.
