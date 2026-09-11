---
title: 版本兼容性
sidebar_position: 1
description: aihub 客户端的版本基线、模块矩阵与升级顺序。
tags: [reference]
---

# 版本兼容性

| 项 | 当前边界 |
| --- | --- |
| 稳定版 | `1.0.0` |
| Maven groupId | `com.whaleal` |
| Java | 8 source / target |
| 文档站 | Node.js `>=20`，发布到 https://whaleal-dev.github.io/aihub/ |

## 模块

| Artifact | 场景 |
| --- | --- |
| `aihub` | 核心客户端 |
| `aihub-spring-boot-starter` | Spring Boot 注入 |
| `aihub-bom` | 多模块版本对齐 |

## 升级顺序

1. 用 `aihub-bom` 固定同一版本（若同时引多个 artifact）。
2. 先升级 `aihub`，跑 [Java 快速开始](/docs/getting-started/quickstart-java)。
3. 再升级 starter。
4. 对照 [上线前检查](/docs/production/production-checklist)。

Provider 能力不对称，以 [平台矩阵](/docs/capabilities/models/platform-service-matrix) 为准。
