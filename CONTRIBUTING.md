# Contributing to aihub

Thanks for your interest in improving aihub. This guide covers reporting issues, proposing features, and landing code changes.

## 1. Target branch

**Open pull requests against `main`.**

## 2. Before you start

- Search [open issues](https://github.com/whaleal-dev/aihub/issues) to avoid duplicates.
- For non-trivial changes, open an issue first to align on scope and API shape.
- aihub is a **Java LLM client**. RAG, MCP, Agent runtimes, and workflow engines are out of scope unless a maintainer explicitly expands the charter.

## 3. Reporting a bug

Use the **Bug report** issue template. Include aihub version, JDK, provider, whether you run under Spring Boot, and a minimal reproduction.

## 4. Requesting a feature

Use the **Feature request** issue template. Describe the use case, the current workaround, and the API surface you would expect.

## 5. Building and testing

Requirements: JDK 8+ and Maven 3.6+.

```bash
mvn clean install -DskipTests
mvn -pl aihub test
mvn test
```

Some tests call live provider endpoints and are gated behind environment variables (for example `OPENAI_API_KEY`). If a key is absent those tests are skipped, not failed.

Documentation changes also need:

```bash
npm --prefix docs-site ci
npm --prefix docs-site run build
```

The docs site is published to GitHub Pages from `main` via `.github/workflows/docs-pages.yml`.

## 6. Code style

- **Indentation:** 4 spaces, no tabs.
- **Types:** `PascalCase` (e.g. `ChatCompletion`, `AiService`).
- **Methods and fields:** `lowerCamelCase` (e.g. `chatCompletion`, `apiKey`).
- **Constants:** `UPPER_SNAKE_CASE` (e.g. `DEFAULT_TIMEOUT`).
- Keep public API binary-compatible with the current minor line; deprecated APIs should be marked `@Deprecated` with a replacement note in the Javadoc.

## 7. Commit and PR conventions

- Branch naming: `feature/<topic>`, `fix/<topic>`, or `docs/<topic>`.
- Commit message prefix: `feat(scope):`, `fix(scope):`, `docs:`, `chore:`. Suggested trailer: `[恒哥]`.
- Fill in the **pull request template**: change type, linked issue, the exact `mvn` / `npm` command you ran, and any breaking change.

## 8. Relationship to AGENTS.md

This file is the contributor guide for humans. `AGENTS.md` is the entry point for coding agents working in this repo.

## 9. Security

Do not open public issues for security vulnerabilities. See [SECURITY.md](SECURITY.md).

## 10. Code of Conduct

Participating in this project means following the [Code of Conduct](CODE_OF_CONDUCT.md).

## License

By contributing, you agree your contributions are licensed under the [Apache License 2.0](LICENSE).
