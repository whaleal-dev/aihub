---
title: 架构与模块地图
description: 当前仓库只有三个 Maven 模块：aihub、starter、BOM。
tags: [concept]
---

# 架构与模块地图

```text
aihub-sdk          （父 POM，不发布业务代码）
├─ aihub           核心客户端
├─ aihub-spring-boot-starter
└─ aihub-bom
```

依赖方向：starter → aihub。BOM 只做版本对齐。

对外文档站是 `docs-site/`，发布在 https://whaleal-dev.github.io/aihub/

读源码建议顺序：`service/factory/AiService.java` → `platform/<厂商>/` → `network/`。
