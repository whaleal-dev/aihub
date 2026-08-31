package com.whaleal.aihub.platform.gemini;

import com.whaleal.aihub.config.GeminiConfig;
import com.whaleal.aihub.platform.openai.chat.OpenAiChatService;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletion;
import com.whaleal.aihub.platform.openai.chat.entity.ChatMessage;
import com.whaleal.aihub.platform.openai.embedding.OpenAiEmbeddingService;
import com.whaleal.aihub.platform.openai.image.OpenAiImageService;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Gemini 命名入口：走官方 OpenAI 兼容层，path 不含多余 {@code v1/}。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
public class GeminiAiServiceTest {

    @Test
    public void factoryReusesOpenAiStackAndDefaultHost() {
        GeminiConfig geminiConfig = new GeminiConfig();
        geminiConfig.setApiKey("gemini-test");

        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());
        configuration.setGeminiConfig(geminiConfig);

        AiService aiService = new AiService(configuration);
        Assert.assertEquals("https://generativelanguage.googleapis.com/v1beta/openai/",
                configuration.getGeminiConfig().getApiHost());
        Assert.assertTrue(aiService.getChatService(PlatformType.GEMINI) instanceof OpenAiChatService);
        Assert.assertTrue(aiService.getEmbeddingService(PlatformType.GEMINI) instanceof OpenAiEmbeddingService);
        Assert.assertTrue(aiService.getImageService(PlatformType.GEMINI) instanceof OpenAiImageService);
    }

    @Test
    public void chatPostsToOpenAiCompatiblePathWithoutExtraV1() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":\"chat-1\",\"object\":\"chat.completion\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"ok\"},\"finish_reason\":\"stop\"}]}"));
        server.start();
        try {
            GeminiConfig geminiConfig = new GeminiConfig();
            geminiConfig.setApiHost(server.url("/").toString());
            geminiConfig.setApiKey("gemini-test");

            Configuration configuration = new Configuration();
            configuration.setOkHttpClient(new OkHttpClient());
            configuration.setGeminiConfig(geminiConfig);

            new AiService(configuration).getChatService(PlatformType.GEMINI)
                    .chatCompletion(ChatCompletion.builder()
                            .model("gemini-2.0-flash")
                            .message(ChatMessage.withUser("hi"))
                            .build());

            RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
            Assert.assertNotNull(request);
            Assert.assertEquals("/chat/completions", request.getPath());
            Assert.assertEquals("Bearer gemini-test", request.getHeader("Authorization"));
        } finally {
            server.shutdown();
        }
    }
}
