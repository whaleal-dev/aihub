<p align="center"><img src="https://capsule-render.vercel.app/api?type=waving&color=0:6A5ACD,100:2E86C1&height=180&section=header&text=aihub&fontSize=46&fontColor=ffffff&animation=fadeIn&desc=Java%20LLM%20client%20for%20JDK%208%2B&descAlignY=68" alt="aihub banner" /></p>
<p align="center"><a href="https://search.maven.org/artifact/io.github.whaleal-dev/aihub"><img src="https://img.shields.io/maven-central/v/io.github.whaleal-dev/aihub?color=2E86C1&label=Maven%20Central" alt="Maven Central" /></a> <a href="https://whaleal-dev.github.io/aihub/"><img src="https://img.shields.io/badge/Docs-GitHub%20Pages-0A7EA4" alt="Docs" /></a> <a href="https://www.apache.org/licenses/LICENSE-2.0.txt"><img src="https://img.shields.io/badge/License-Apache%202.0-1F6FEB" alt="License" /></a> <img src="https://img.shields.io/badge/JDK-8%2B-2EA043" alt="JDK 8+" /> <a href="https://www.orcarouter.ai/ref/ref_76f80b88a83930469424"><img src="https://img.shields.io/badge/Powered_by-OrcaRouter-2563eb" alt="Powered by OrcaRouter" /></a></p>

# aihub

**aihub 是面向 JDK 8+ 的 Java 大模型客户端**，不是 Agent 平台、不是工作流引擎、也不是 RAG 框架。

它只做一件事：把各厂商的 HTTP / SSE API 收成同一套 Java 调用。

- 版本：`1.0.0`
- 坐标：`io.github.whaleal-dev:aihub`
- GitHub：[whaleal-dev/aihub](https://github.com/whaleal-dev/aihub)
- 维护者：恒哥

[English README](README-EN.md)

## 项目范围

### 范围内（本仓库提供）

| 能力 | 说明 |
|------|------|
| Chat / Completions | 同步与流式（SSE）对话 |
| Responses / Messages | OpenAI Responses、Anthropic Messages 等新形态 API |
| Embedding | 文本向量化 |
| 媒体 API | 图像、音频、视频、音乐、Realtime |
| Rerank | Jina / Ollama / Doubao 的重排序接口 |
| 多厂商适配 | 同一套 `IChatService` 打到 OpenAI、通义、DeepSeek 等 |
| Tool 协议字段 | 请求里带 `tools`，响应里解析 `tool_calls`；可选把 `@FunctionCall` 编成 JSON Schema |
| 鉴权、超时、代理、错误映射 | 客户端基础设施 |
| Spring Boot Starter | 把上述客户端注入 Spring |

### 范围外（不提供，请在业务侧自行实现）

- RAG：切分、向量库、混合检索、引用拼装
- MCP Client / Server / Gateway
- Agent 循环、审批、Team、A2A
- 工作流引擎（Dify / Coze / n8n / FlowGram）
- Coding Agent CLI / TUI / ACP
- 会话记忆持久化、插件生态、沙箱执行

一句话：**客户端把请求发出去、把响应解析回来；不负责检索知识库，也不负责执行工具或编排多步任务。**

## 安装

- Gradle：`implementation 'io.github.whaleal-dev:aihub:1.0.0'`
- Maven：`<dependency><groupId>io.github.whaleal-dev</groupId><artifactId>aihub</artifactId><version>1.0.0</version></dependency>`

Spring Boot 使用 `io.github.whaleal-dev:aihub-spring-boot-starter`。

推送分支 `release-x.y.z` 会按 quick-sms 同样方式发布到 Maven Central（`mvn -Pcentral deploy`）。

## 30 秒跑通

设置 `OPENAI_API_KEY` 后，下面代码即可发出第一条请求：

```java
import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletion;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletionResponse;
import com.whaleal.aihub.platform.openai.chat.entity.ChatMessage;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IChatService;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;

public class AihubFirstChat {
    public static void main(String[] args) {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        openAiConfig.setApiKey(System.getenv("OPENAI_API_KEY"));
        Configuration configuration = new Configuration();
        configuration.setOpenAiConfig(openAiConfig);
        AiService aiService = new AiService(configuration);
        IChatService chatService = aiService.getChatService(PlatformType.OPENAI);
        ChatCompletion request = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.withUser("用一句话介绍 aihub"))
                .build();
        ChatCompletionResponse response = chatService.chatCompletion(request);
        System.out.println(response.getChoices().get(0).getMessage().getContent().getText());
    }
}
```

换成 DashScope / DeepSeek / Ollama：替换 `PlatformType` 与对应 Config，其余代码不变。

## 模块

| 模块 | 说明 |
|------|------|
| `aihub` | 核心客户端 |
| `aihub-spring-boot-starter` | Spring Boot 自动配置 |
| `aihub-bom` | 版本对齐 |

## 支持的平台

- OpenAI（及兼容网关）
- OrcaRouter
- Anthropic
- Gemini
- Grok
- DashScope（通义/百炼）
- Doubao（火山方舟）
- DeepSeek
- Moonshot
- Zhipu（智谱）
- Hunyuan（混元）
- Lingyi（零一万物）
- Ollama
- MiniMax
- Baichuan
- Jina
- Suno

## License

Copyright 2026 whaleal-dev

[Apache License 2.0](LICENSE)
