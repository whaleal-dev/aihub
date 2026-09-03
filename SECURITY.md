# Security Policy

## Supported Versions

aihub 1.0.0 is the first release of this client-only tree.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

If you are on an older 2.x artifact from the former monorepo, upgrade to `1.0.0` before reporting a vulnerability.

## Reporting a Vulnerability

**Do NOT open a public GitHub issue for security vulnerabilities.**

Please report suspected vulnerabilities privately via [GitHub Security Advisories](https://github.com/whaleal-dev/aihub/security/advisories/new).

- Subject prefix: `[SECURITY] aihub - <short summary>`

Include the following:

1. aihub version and affected module (`aihub` or `aihub-spring-boot-starter`)
2. JDK version and runtime environment
3. Provider / platform involved (OpenAI, Anthropic, DashScope, Ollama, etc.)
4. Minimal reproduction steps
5. Impact assessment and any known mitigations

## Response Timeline

| Stage              | Target       |
| ------------------ | ------------ |
| Acknowledgement    | within 24 hours of report |
| Initial assessment | within 72 hours |
| Fix or mitigation  | within 30 days for high severity, 90 days for medium/low |
| Public disclosure  | after a fix is released, or after 90 days from report (Coordinated Disclosure) |

## Scope

In scope:

- Vulnerabilities in aihub source that allow credential leakage, request forgery, or denial of service when the library is used as documented.
- Supply-chain concerns in published artifacts under `io.github.whaleal-dev` on Maven Central.

Out of scope:

- Vulnerabilities in upstream LLM provider APIs.
- Issues that require the application to already have been compromised.
- Rate limiting, billing, or quota enforcement on the provider side.
- Security of RAG / MCP / Agent systems you build **on top of** this client.

## Secure Usage Reminders

- Never hard-code API keys in source files, tests, or configuration committed to version control.
- Treat model `tool_calls` as untrusted instructions: this client parses the protocol fields; your application decides what to execute.
- Do not log full Authorization headers or API keys.
