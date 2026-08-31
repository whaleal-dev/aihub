---
sidebar_position: 1
title: GitHub Pages 部署
description: aihub 文档站发布在 GitHub Pages 默认地址 whaleal-dev.github.io/aihub。
tags: [how-to]
---

# GitHub Pages 部署

公开文档地址：

**https://whaleal-dev.github.io/aihub/**

推送到 `main` 且改动了 `docs-site/` 时，`.github/workflows/docs-pages.yml` 会构建并发布。

## 站点配置

| 项 | 值 |
|----|----|
| `url` | `https://whaleal-dev.github.io` |
| `baseUrl` | `/aihub/` |
| 构建目录 | `docs-site/build` |
| 工作流 | `.github/workflows/docs-pages.yml` |

不要改成自定义域名，除非维护者明确切换。

## 本地验证（与线上同一条命令）

```bash
npm --prefix docs-site ci
npm --prefix docs-site run typecheck
npm --prefix docs-site run build
```

Docusaurus 开启了 `onBrokenLinks: throw`。链到已删除的 Agent / RAG / MCP 页面会让 GitHub Pages 构建失败。

旧 URL（例如 `/docs/agent/overview`）由 `redirects.generated.json` 转到 [文档首页](/docs/intro)。

## 发布后核对

1. https://whaleal-dev.github.io/aihub/ 能打开
2. https://whaleal-dev.github.io/aihub/docs/intro 能打开
3. 首页不再出现 Agent / MCP / Coding Agent 入口
