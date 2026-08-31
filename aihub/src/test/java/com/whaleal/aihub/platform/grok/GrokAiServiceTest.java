package com.whaleal.aihub.platform.grok;

import com.whaleal.aihub.config.GrokConfig;
import com.whaleal.aihub.platform.grok.video.GrokVideoService;
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
 * Grok 命名入口：Chat / Embedding / Image 复用 OpenAI 栈，Video 仍走 Grok 方言。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
public class GrokAiServiceTest {

    @Test
    public void factoryReusesOpenAiStackAndDefaultHost() {
        GrokConfig grokConfig = new GrokConfig();
        grokConfig.setApiKey("xai-test");

        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());
        configuration.setGrokConfig(grokConfig);

        AiService aiService = new AiService(configuration);
        Assert.assertEquals("https://api.x.ai/", configuration.getGrokConfig().getApiHost());
        Assert.assertTrue(aiService.getChatService(PlatformType.GROK) instanceof OpenAiChatService);
        Assert.assertTrue(aiService.getEmbeddingService(PlatformType.GROK) instanceof OpenAiEmbeddingService);
        Assert.assertTrue(aiService.getImageService(PlatformType.GROK) instanceof OpenAiImageService);
        Assert.assertTrue(aiService.getVideoService(PlatformType.GROK) instanceof GrokVideoService);
    }

    @Test
    public void chatPostsToOpenAiCompatiblePath() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":\"chat-1\",\"object\":\"chat.completion\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"ok\"},\"finish_reason\":\"stop\"}]}"));
        server.start();
        try {
            GrokConfig grokConfig = new GrokConfig();
            grokConfig.setApiHost(server.url("/").toString());
            grokConfig.setApiKey("xai-test");

            Configuration configuration = new Configuration();
            configuration.setOkHttpClient(new OkHttpClient());
            configuration.setGrokConfig(grokConfig);

            new AiService(configuration).getChatService(PlatformType.GROK)
                    .chatCompletion(ChatCompletion.builder()
                            .model("grok-2")
                            .message(ChatMessage.withUser("hi"))
                            .build());

            RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
            Assert.assertNotNull(request);
            Assert.assertEquals("/v1/chat/completions", request.getPath());
            Assert.assertEquals("Bearer xai-test", request.getHeader("Authorization"));
        } finally {
            server.shutdown();
        }
    }
}
