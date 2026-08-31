<p align="center"><img src="https://capsule-render.vercel.app/api?type=waving&color=0:6A5ACD,100:2E86C1&height=180&section=header&text=aihub&fontSize=46&fontColor=ffffff&animation=fadeIn&desc=Java%20LLM%20client%20for%20JDK%208%2B&descAlignY=68" alt="aihub banner" /></p>
<p align="center"><a href="https://search.maven.org/artifact/com.whaleal/aihub"><img src="https://img.shields.io/maven-central/v/com.whaleal/aihub?color=2E86C1&label=Maven%20Central" alt="Maven Central" /></a> <a href="https://whaleal-dev.github.io/aihub/"><img src="https://img.shields.io/badge/Docs-GitHub%20Pages-0A7EA4" alt="Docs" /></a> <a href="https://www.apache.org/licenses/LICENSE-2.0.txt"><img src="https://img.shields.io/badge/License-Apache%202.0-1F6FEB" alt="License" /></a> <img src="https://img.shields.io/badge/JDK-8%2B-2EA043" alt="JDK 8+" /></p>

# aihub

**aihub is a JDK 8+ Java LLM client.** It is not an agent platform, not a workflow engine, and not a RAG framework.

It does one job: wrap vendor HTTP / SSE APIs behind a single Java surface.

- Version: `1.0.0`
- Coordinates: `com.whaleal:aihub`
- GitHub: [whaleal-dev/aihub](https://github.com/whaleal-dev/aihub)
- Maintainer: 恒哥

[中文 README](README.md)

## Scope

### In scope

| Capability | What you get |
|------------|----------------|
| Chat / Completions | Sync and streaming (SSE) chat |
| Responses / Messages | OpenAI Responses, Anthropic Messages, and similar APIs |
| Embedding | Text embeddings |
| Media APIs | Image, audio, video, music, Realtime |
| Rerank | Jina / Ollama / Doubao rerank endpoints |
| Multi-provider adapters | One `IChatService` for OpenAI, DashScope, DeepSeek, and others |
| Tool protocol fields | Send `tools` in the request, parse `tool_calls` in the response; optionally compile `@FunctionCall` into JSON Schema |
| Auth, timeouts, proxy, error mapping | Client infrastructure |
| Spring Boot starter | Wire the client into Spring |

### Out of scope

- RAG: chunking, vector stores, hybrid retrieval, citation assembly
- MCP client / server / gateway
- Agent loops, approvals, teams, A2A
- Workflow engines (Dify / Coze / n8n / FlowGram)
- Coding Agent CLI / TUI / ACP
- Persistent chat memory, plugin ecosystems, sandbox execution

In short: **the client sends requests and parses responses. It does not retrieve a knowledge base, execute tools, or orchestrate multi-step tasks.**

## Install

- Gradle: `implementation 'com.whaleal:aihub:1.0.0'`
- Maven: `<dependency><groupId>com.whaleal</groupId><artifactId>aihub</artifactId><version>1.0.0</version></dependency>`

For Spring Boot, use `com.whaleal:aihub-spring-boot-starter`.

## First chat in 30 seconds

Set `OPENAI_API_KEY`, then:

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
                .message(ChatMessage.withUser("Introduce aihub in one sentence"))
                .build();
        ChatCompletionResponse response = chatService.chatCompletion(request);
        System.out.println(response.getChoices().get(0).getMessage().getContent().getText());
    }
}
```

Switch to DashScope / DeepSeek / Ollama by changing `PlatformType` and the matching Config.

## Modules

| Module | Role |
|--------|------|
| `aihub` | Core client |
| `aihub-spring-boot-starter` | Spring Boot auto-configuration |
| `aihub-bom` | Version alignment |

## Platforms

- OpenAI (and compatible gateways)
- OrcaRouter
- Anthropic
- Gemini
- Grok
- DashScope
- Doubao
- DeepSeek
- Moonshot
- Zhipu
- Hunyuan
- Lingyi
- Ollama
- MiniMax
- Baichuan
- Jina
- Suno

## License

Copyright 2026 whaleal-dev

[Apache License 2.0](LICENSE)
