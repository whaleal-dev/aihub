---
sidebar_position: 1
title: GitHub Pages 部署
description: aihub 文档站发布在 whaleal.com/aihub，由 Actions 构建 docs-site。
tags: [how-to]
---

# GitHub Pages 部署

公开文档地址：

**https://whaleal.com/aihub/**

不要用 GitHub 的 `jekyll-gh-pages.yml`。那个模板从仓库根目录跑 Jekyll，首页会变成 `README.md`。

发布工作流是 [`.github/workflows/docs-pages.yml`](https://github.com/whaleal-dev/aihub/blob/main/.github/workflows/docs-pages.yml)：推送到 `main` 就会构建 `docs-site/` 并部署。Actions 里名叫 **Deploy GitHub Pages**。截图里的 `java-regression` 是 Java CI，和文档站无关。

## 仓库设置

Settings → Pages：

1. **Source** 选 **GitHub Actions**
2. 不要选 Deploy from a branch，也不要选 Jekyll / Static HTML
3. 自定义域名保持 `whaleal.com`

## 站点配置

| 项 | 值 |
|----|------|
| `url` | `https://whaleal.com` |
| `baseUrl` | `/aihub/` |
| 构建目录 | `docs-site/build` |
| 工作流 | `.github/workflows/docs-pages.yml` |

## 本地验证（与线上同一条命令）

```bash
npm --prefix docs-site ci
npm --prefix docs-site run typecheck
npm --prefix docs-site run build
```

Docusaurus 开启了 `onBrokenLinks: throw`。链到已删除的 Agent / RAG / MCP 页面会让 GitHub Pages 构建失败。

旧 URL（例如 `/docs/agent/overview`）由 `redirects.generated.json` 转到 [文档首页](/docs/intro)。

## 发布后核对

1. https://whaleal.com/aihub/ 能打开，且是带导航的文档站（不是 README）
2. https://whaleal.com/aihub/docs/intro 能打开
3. 首页不再出现 Agent / MCP / Coding Agent 入口
