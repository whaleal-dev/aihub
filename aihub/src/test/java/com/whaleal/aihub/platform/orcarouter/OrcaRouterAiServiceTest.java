package com.whaleal.aihub.platform.orcarouter;

import com.whaleal.aihub.config.OrcaRouterConfig;
import com.whaleal.aihub.interceptor.OrcaRouterCostHeaderInterceptor;
import com.whaleal.aihub.platform.openai.audio.OpenAiAudioService;
import com.whaleal.aihub.platform.openai.chat.OpenAiChatService;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletion;
import com.whaleal.aihub.platform.openai.chat.entity.ChatMessage;
import com.whaleal.aihub.platform.openai.embedding.OpenAiEmbeddingService;
import com.whaleal.aihub.platform.openai.image.OpenAiImageService;
import com.whaleal.aihub.platform.openai.response.OpenAiResponsesService;
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
 * OrcaRouter 命名入口：复用 OpenAI 兼容栈，不另写协议适配。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
public class OrcaRouterAiServiceTest {

    @Test
    public void factoryReusesOpenAiServicesAndDefaultHost() {
        OrcaRouterConfig orcaRouterConfig = new OrcaRouterConfig();
        orcaRouterConfig.setApiKey("sk-orca-test");

        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());
        configuration.setOrcaRouterConfig(orcaRouterConfig);

        AiService aiService = new AiService(configuration);
        Assert.assertEquals("https://api.orcarouter.ai/", configuration.getOrcaRouterConfig().getApiHost());
        Assert.assertTrue(aiService.getChatService(PlatformType.ORCAROUTER) instanceof OpenAiChatService);
        Assert.assertTrue(aiService.getEmbeddingService(PlatformType.ORCAROUTER) instanceof OpenAiEmbeddingService);
        Assert.assertTrue(aiService.getAudioService(PlatformType.ORCAROUTER) instanceof OpenAiAudioService);
        Assert.assertTrue(aiService.getImageService(PlatformType.ORCAROUTER) instanceof OpenAiImageService);
        Assert.assertTrue(aiService.getResponsesService(PlatformType.ORCAROUTER) instanceof OpenAiResponsesService);
    }

    @Test
    public void chatPostsToOpenAiCompatiblePathWithCostHeader() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":\"chat-1\",\"object\":\"chat.completion\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"ok\"},\"finish_reason\":\"stop\"}]}"));
        server.start();
        try {
            OrcaRouterConfig orcaRouterConfig = new OrcaRouterConfig();
            orcaRouterConfig.setApiHost(server.url("/").toString());
            orcaRouterConfig.setApiKey("sk-orca-test");
            orcaRouterConfig.setIncludeCost(true);

            Configuration configuration = new Configuration();
            configuration.setOkHttpClient(new OkHttpClient());
            configuration.setOrcaRouterConfig(orcaRouterConfig);

            new AiService(configuration).getChatService(PlatformType.ORCAROUTER)
                    .chatCompletion(ChatCompletion.builder()
                            .model("orcarouter/auto")
                            .message(ChatMessage.withUser("hi"))
                            .build());

            RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
            Assert.assertNotNull(request);
            Assert.assertEquals("/v1/chat/completions", request.getPath());
            Assert.assertEquals("Bearer sk-orca-test", request.getHeader("Authorization"));
            Assert.assertEquals("true", request.getHeader(OrcaRouterCostHeaderInterceptor.HEADER_NAME));
        } finally {
            server.shutdown();
        }
    }
}
