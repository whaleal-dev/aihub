---
title: Spring Boot 配置参考
description: 按 aihub.* 能力面前缀梳理 AIHub 的 Spring Boot 配置，说明单实例与多实例注册表配置的流向与分层判断。
tags: [reference]
---

# Spring Boot 配置参考
这一页只讲配置入口，不讲业务调用。

## 1. 配置分层

AIHub 的 Spring Boot 配置不是一坨平铺字段，而是按能力面分层组织的。

常见前缀包括：

- `aihub.openai.*`
- `aihub.orcarouter.*`
- `aihub.doubao.*`
- `aihub.dashscope.*`
- `aihub.ollama.*`
- `aihub.jina.*`
- `aihub.okhttp.*`
- `aihub.platforms[]`
- `aihub.vector.*`
- `aihub.agentflow.*`
- `aihub.extensions.*`
- `aihub.flowgram.*`

## 2. 这些配置最终流向哪里

可以先把主线记成：

```text
application.yml
  -> *ConfigProperties
  -> AiConfigAutoConfiguration
  -> Configuration / Bean graph
```

所以这页的重点不是字段列表本身，而是：

- 这组字段属于哪个能力面
- 它会进入单实例主线，还是多实例注册表主线

## 3. 单实例和多实例

### 单实例

像 `aihub.openai.*` 这种配置，适合最直接的 provider 接入。

OpenAI-compatible 中转平台也属于这一类。比如 TroveBox：

```yaml
aihub:
  openai:
    api-key: ${TROVEBOX_API_KEY}
    api-host: https://codex.trovebox.online/
```

此时业务代码仍然从 `AiService` 获取 `PlatformType.OPENAI` 的服务。

OrcaRouter 有独立前缀，走命名入口而不是改 `aihub.openai.api-host`：

```yaml
aihub:
  orcarouter:
    api-key: ${ORCAROUTER_API_KEY}
    # 默认 https://api.orcarouter.ai/
    include-cost: false
```

```java
IChatService chatService = aiService.getChatService(PlatformType.ORCAROUTER);
```

### 多实例

像 `aihub.platforms[]` 这种配置，适合构建 `AiServiceRegistry`，用于多账号、多租户或多平台路由。

两条线不是互斥，而是粒度不同。

示例：

```yaml
aihub:
  platforms:
    - id: openai-main
      platform: openai
      api-key: ${OPENAI_API_KEY}
      api-host: https://api.openai.com/
    - id: trovebox-low-cost
      platform: openai
      api-key: ${TROVEBOX_API_KEY}
      api-host: https://codex.trovebox.online/
```

```java
IChatService chatService = aiServiceRegistry.getChatService("trovebox-low-cost");
```

`id` 是业务路由名；`platform` 决定底层 provider 适配。多个 OpenAI-compatible endpoint 可以共享 `platform: openai`，只通过不同 `id` 和 `api-host` 区分。OrcaRouter 用 `platform: orcarouter`。

## 4. `aihub.okhttp.*` 的位置

`aihub.okhttp.*` 不是 provider 配置，而是底层网络栈配置，绑定类 `OkHttpConfigProperties`（前缀 `aihub.okhttp`）。它影响的是：

- 日志级别
- 超时时间
- 代理
- SSL 策略

这类配置会通过 `AiConfigAutoConfiguration.initOkHttp()` 进入整个 starter 共享的统一 `OkHttpClient`。

### 完整字段与默认值

| 字段 | 默认值 | 含义 |
| --- | --- | --- |
| `connect-timeout` | `300`（秒） | 连接超时 |
| `write-timeout` | `300`（秒） | 写超时 |
| `read-timeout` | `300`（秒） | 读超时 |
| `time-unit` | `SECONDS` | 上面三个超时的单位 |
| `log` | `BASIC` | OkHttp 日志级别（`NONE`/`BASIC`/`HEADERS`/`BODY`） |
| `proxy-type` | `HTTP` | 代理类型（`HTTP`/`SOCKS`/`DIRECT`） |
| `proxy-url` | 空 | 代理主机 |
| `proxy-port` | `0` | 代理端口 |
| `ignore-ssl` | `false` | 是否跳过 SSL 证书校验 |

示例：

```yaml
aihub:
  okhttp:
    connect-timeout: 15
    read-timeout: 60
    time-unit: seconds
    log: basic
    ignore-ssl: false
    proxy-type: HTTP
    proxy-url: 127.0.0.1
    proxy-port: 7890
```

### `ignore-ssl`：默认关闭，显式才打开

`ignore-ssl` 默认 `false` —— 生产环境不应跳过证书校验。历史上它用于请求某些证书不全的平台（如 Moonshot/Kimi），现在只有显式设 `aihub.okhttp.ignore-ssl=true` 才会装 trust-all 的 `SSLSocketFactory` 和放行 hostname 的 `HostnameVerifier`。除非你明确知道目标证书不可信，否则保持 `false`。

:::warning trust-all 是安全降级
打开 `ignore-ssl=true` 等于放弃对该客户端所有请求的证书校验，属于安全降级，仅在受控内网或临时联调时使用。
:::

### OkHttp SPI 扩展点

并发调度与连接池不是写死的，由 SPI 提供（详见 [Auto Configuration / OkHttp SPI 扩展点](/docs/integrations/spring-boot/auto-configuration#7-okhttp-spi-扩展点)）：

- `DispatcherProvider`（默认 `DefaultDispatcherProvider`）
- `ConnectionPoolProvider`（默认 `DefaultConnectionPoolProvider`）

要换实现，走 Java SPI（`META-INF/services`）注册即可，无需改 starter。

## 6. 这页应该怎么用

当你要加一个新环境配置时，先问自己：

1. 这是 provider 级参数，还是 HTTP 栈参数
2. 这是单实例配置，还是多实例注册表配置

## 7. 关键对象

- `AiConfigProperties`
- 各类 `*ConfigProperties`
- `AiConfigAutoConfiguration`
- `Configuration`

## 8. 继续阅读

- 首次接入：看 [Spring Boot 快速开始](/docs/getting-started/quickstart-spring-boot)
- 中转平台：看 [OpenAI-compatible 与 TroveBox](/docs/capabilities/models/openai-compatible-and-trovebox)
- 多实例入口：看 [服务入口与注册表](/docs/capabilities/service-entry)
