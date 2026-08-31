# Repository Guidelines

## Project Identity

- **Project**: `aihub-sdk`
- **What it is**: a JDK 8+ Java LLM **client**. It sends HTTP / SSE requests and parses responses.
- **What it is not**: RAG, MCP, Agent runtime, workflow engine, Coding CLI, or plugin host.
- **Primary language**: Java 8
- **Build tool**: Maven
- **Public docs**: `docs-site/` deployed to GitHub Pages at https://whaleal-dev.github.io/aihub/

## Modules

| Path | Role |
|------|------|
| `aihub/` | Core client: Chat / Responses / Messages, embedding, image / audio / video / music / realtime, rerank, auth, SSE |
| `aihub-spring-boot-starter/` | Spring Boot auto-configuration for the client |
| `aihub-bom/` | Version alignment BOM |
| `docs-site/` | Docusaurus site (GitHub Pages) |

Parent POM artifact is `aihub-sdk` (`packaging=pom`). Do not reintroduce deleted modules.

## Hard Rules

1. Keep Java 8 unless a task explicitly upgrades the baseline.
2. Never hardcode secrets, provider keys, or machine-local paths. Use env vars.
3. Core behavior lives in `aihub/`. The starter only wires Spring. Docs are not the source of production logic.
4. When a fixed regression surface changes, update `docs/05-TEST-QA/Regression-SSoT.md` and `docs/05-TEST-QA/Cadence-Ledger.md`.
5. Do not add planning / walkthrough files under repo root or `docs/09-PLANNING/` / `docs/10-WALKTHROUGH/`.
6. Public docs on GitHub Pages must match the client-only scope. Do not document RAG / MCP / Agent / FlowGram as if they shipped here.
7. `@author` on existing Java files is not rewritten; new files use `@author 恒哥`, edits append `@author 恒哥`.
8. Git author/committer is only `hbn.king <hbn.king@gmail.com>`. Never add `cursoragent` / `Cursor` as `@author`, POM developer, or `Co-authored-by` trailer. If a commit still contains `Co-authored-by: Cursor <cursoragent@cursor.com>`, strip it before push.

## Build And Test

```bash
mvn -DskipTests package
mvn -pl aihub -DskipTests=false test
mvn -pl aihub-spring-boot-starter -am -DskipTests=false test
```

Docs site (required before merging `docs-site/` changes; this is what GitHub Pages builds):

```bash
npm --prefix docs-site ci
npm --prefix docs-site run typecheck
npm --prefix docs-site run build
```

Live provider tests need credentials and are excluded from the default run.

## Task-Type Reading Matrix

| Task type | Read first |
|-----------|------------|
| Core client / provider / Chat / media / rerank | `AGENTS.md`, `README.md` |
| Spring Boot starter | `AGENTS.md`, `docs-site/docs/integrations/spring-boot/overview.md` |
| Docs / GitHub Pages | `docs-site/docs/reference/maintainers/github-pages.md`, `docs/05-TEST-QA/Regression-SSoT.md` |
| Regression / verification | `docs/11-REFERENCE/testing-standard.md`, `docs/05-TEST-QA/Regression-SSoT.md` |

## Review Focus

1. Public API breakage in `aihub`
2. Java 8 or starter regressions
3. Docs that still describe deleted modules (breaks GitHub Pages mental model and often `onBrokenLinks`)
4. Secrets in samples

## Scope Limits

These bound what you **propose**. Report anything that is actually wrong.

1. This is a cooperating-operator client library, not a security paper.
2. Do not add hashes, feature flags, or compat layers for cases that do not occur here.
3. Do not rebuild Agent / RAG / MCP inside this repo because a doc or changelog still mentions them.
4. Where judgement is needed, judge. Do not manufacture findings.
