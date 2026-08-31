# docs-site

aihub 官方 Docusaurus 文档站。发布地址：https://whaleal-dev.github.io/aihub/

## 本地

```bash
cd docs-site
npm install
npm start
```

## 构建（与 GitHub Pages 相同）

```bash
npm run typecheck
npm run build
```

推送到 `main` 后由 `.github/workflows/docs-pages.yml` 自动发布。

## 目录

- `docs/getting-started/` 快速开始
- `docs/capabilities/` 模型、媒体、工具协议
- `docs/integrations/spring-boot/` starter
- `docs/reference/` FAQ 与维护者说明
