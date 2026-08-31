---
title: 发布与制品
sidebar_position: 2
description: AIHub 的发布 artifact、Maven 坐标与 BOM 版本对齐策略，说明各模块角色、依赖引入方式与版本升级顺序。
tags: [reference]
---

# 发布与制品
这页说明 AIHub 的发布 artifact、版本对齐和项目引入顺序。它面向使用者和维护者，不替代每个模块的 API 文档。

## Maven 坐标

AIHub 当前发布坐标使用：

```xml
<groupId>com.whaleal</groupId>
```

当前仓库版本为：

```xml
<version>1.0.0</version>
```

## 推荐依赖方式

只引入一个模块时，可以直接声明该模块版本：

```xml
<dependency>
    <groupId>com.whaleal</groupId>
    <artifactId>aihub</artifactId>
    <version>1.0.0</version>
</dependency>
```

引入多个 AIHub 模块时，推荐使用 BOM 对齐：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.whaleal</groupId>
            <artifactId>aihub-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后业务依赖里不再重复写版本：

```xml
<dependency>
    <groupId>com.whaleal</groupId>
    <artifactId>aihub-spring-boot-starter</artifactId>
</dependency>
```

## Artifact 角色

| Artifact | 角色 | 何时引入 |
| --- | --- | --- |
| `aihub` | 核心客户端 | 普通 Java 项目 |
| `aihub-spring-boot-starter` | Spring 接入 | Spring Boot 应用 |
| `aihub-bom` | 版本对齐 | 引入两个以上 artifact |

父 POM 是聚合入口，业务项目不要依赖 `aihub-sdk` 本身。

## 发布边界

父 POM 是多模块发布入口，但根 artifact 默认不应被业务项目当成 SDK 使用。项目接入时只引入需要的模块。

发布 profile 会处理 source、javadoc、GPG 签名和 Sonatype Central 发布配置。完整发布步骤见 [发布检查清单](/docs/reference/maintainers/release-checklist)。维护者发布前应确认：

- 版本号已在根 POM 和模块 POM 中一致更新。
- `aihub-bom` 已包含需要对齐的发布模块。
- live provider 测试和本地测试边界清楚。
- release profile 使用的凭证不写入仓库。

## 依赖选择示例

### 普通 Java 最小接入

```xml
<dependency>
    <groupId>com.whaleal</groupId>
    <artifactId>aihub</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Spring Boot 接入

```xml
<dependency>
    <groupId>com.whaleal</groupId>
    <artifactId>aihub-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 版本升级策略

1. 先在测试分支升级 BOM 或单模块版本。
2. 跑最小 quickstart，确认 provider、baseUrl、apiKey 来源仍然正确。
3. 如果使用 Spring Boot starter，检查配置项是否仍能绑定。

升级完成后，把项目内部的接入说明链接回 [版本兼容性](/docs/reference/version-compatibility) 和 [上线前检查](/docs/production/production-checklist)。
