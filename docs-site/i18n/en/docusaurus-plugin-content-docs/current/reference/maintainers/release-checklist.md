---
title: 发布检查清单
sidebar_position: 3
description: 维护者把 AIHub 发布到 Maven Central 与 GitHub Release 的前后检查清单，覆盖版本策略、本地验证与发布后核对。
tags: [reference]
---

# 发布检查清单
这页是维护者发布 AIHub 到 Maven Central 和 GitHub Release 前后的最小检查清单。

## 版本策略

AIHub 当前采用 **全模块同号发布**：

- 发布版：所有发布模块使用同一个稳定版本，例如 `1.0.0`
- 开发分支：所有 Maven POM 使用下一个 `SNAPSHOT`，例如 `1.0.1-SNAPSHOT`
- README / docs 示例：写最新已发布稳定版，不写 `SNAPSHOT`

只改 README、docs-site 或 demo 时，不需要发布新的 Maven 版本。

## 发布前

1. 确认当前分支干净并从 `main` 切出 release 修复分支。
2. 把所有 Maven POM 从 `*-SNAPSHOT` 改成同一个 release 版本。
3. 同步 README、README-EN 和 docs-site 里的用户安装版本。
4. 确认 `aihub-bom` 覆盖需要对齐的发布模块。
5. 确认 `central` profile 不发布聚合根 `aihub-sdk`。
6. 确认仓库 Actions Secrets 已配置 `MAVEN_CENTRAL_USERNAME` / `MAVEN_CENTRAL_PASSWORD` / `MAVEN_GPG_PRIVATE_KEY` / `MAVEN_GPG_PASSPHRASE`（与 quick-sms 相同）。
7. 确认 GPG 私钥可签名（CI 使用 `--pinentry-mode loopback`）。

## 本地验证

```bash
mvn -DskipTests package
mvn -Pcentral -DskipTests clean verify
```

如果修改 docs-site：

```bash
npm --prefix docs-site ci
npm --prefix docs-site run build
```

## 发布

推送 `release-x.y.z`（例如 `release-1.0.0`）会跑 [publish-maven-central.yml](https://github.com/whaleal-dev/aihub/blob/main/.github/workflows/publish-maven-central.yml)：`mvn -Pcentral clean deploy`，`autoPublish` 等到 Central `PUBLISHED`。

本地：

```bash
mvn -Pcentral -DskipTests clean deploy
```

## 发布后验证

1. Maven Central deployment 状态为 `PUBLISHED`。
2. `maven-metadata.xml` 的 `latest` 和 `release` 等于本次版本。
3. 主模块的 `pom`、`jar`、`sources`、`javadoc` 和 `.asc` 可下载。
4. 发布模块是 `aihub` 与 `aihub-spring-boot-starter`（外加 BOM）。
5. 创建 GitHub tag / Release，说明版本变化和 Maven 坐标。
6. 新分支把所有 Maven POM bump 到下一个 `SNAPSHOT` 并合回 `main`。

## 收口

- 删除已合并 release / bump 分支。
- 确认本地 `main` 与 `origin/main` 对齐。
- HA task 记录 deployment id、GitHub release URL、验证命令和残余风险。

