package com.whaleal.aihub;

import com.whaleal.aihub.platform.openai.chat.OpenAiChatService;
import com.whaleal.aihub.platform.openai.video.OpenAiVideoService;
import com.whaleal.aihub.service.IChatService;
import com.whaleal.aihub.service.IVideoService;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.service.factory.AiServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class AiServiceFirstChatAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AiConfigAutoConfiguration.class);

    @Test
    public void starterFirstChatPathShouldExposeAiServiceAndOpenAiChatService() {
        contextRunner
                .withPropertyValues(
                        "aihub.openai.api-key=unit-test-key",
                        "aihub.openai.api-host=https://unit.test/",
                        "aihub.openai.video-url=v1/custom-videos"
                )
                .run(context -> {
                    Assert.assertTrue(context.containsBean("aiService"));

                    AiService aiService = context.getBean(AiService.class);
                    Assert.assertNotNull(aiService.getConfiguration().getOkHttpClient());
                    Assert.assertEquals("unit-test-key", aiService.getConfiguration().getOpenAiConfig().getApiKey());
                    Assert.assertEquals("https://unit.test/", aiService.getConfiguration().getOpenAiConfig().getApiHost());
                    Assert.assertEquals("v1/custom-videos", aiService.getConfiguration().getOpenAiConfig().getVideoUrl());

                    IChatService chatService = aiService.getChatService(PlatformType.OPENAI);
                    Assert.assertTrue(chatService instanceof OpenAiChatService);
                    IVideoService videoService = aiService.getVideoService(PlatformType.OPENAI);
                    Assert.assertTrue(videoService instanceof OpenAiVideoService);
                });
    }

    @Test
    public void starterMultiInstanceShouldBindOpenAiMediaUrls() {
        contextRunner
                .withPropertyValues(
                        "aihub.platforms[0].id=chatfire",
                        "aihub.platforms[0].platform=openai",
                        "aihub.platforms[0].api-key=chatfire-key",
                        "aihub.platforms[0].api-host=https://api.chatfire.cn/",
                        "aihub.platforms[0].image-generation-url=v1/images/generations",
                        "aihub.platforms[0].responses-url=v1/responses",
                        "aihub.platforms[0].video-url=v1/videos"
                )
                .run(context -> {
                    AiServiceRegistry registry = context.getBean(AiServiceRegistry.class);
                    AiService aiService = registry.getAiService("chatfire");

                    Assert.assertEquals("chatfire-key", aiService.getConfiguration().getOpenAiConfig().getApiKey());
                    Assert.assertEquals("https://api.chatfire.cn/", aiService.getConfiguration().getOpenAiConfig().getApiHost());
                    Assert.assertEquals("v1/images/generations", aiService.getConfiguration().getOpenAiConfig().getImageGenerationUrl());
                    Assert.assertEquals("v1/responses", aiService.getConfiguration().getOpenAiConfig().getResponsesUrl());
                    Assert.assertEquals("v1/videos", aiService.getConfiguration().getOpenAiConfig().getVideoUrl());
                    Assert.assertTrue(registry.getVideoService("chatfire") instanceof OpenAiVideoService);
                });
    }

    @Test
    public void starterShouldBindOrcaRouterNamedEntry() {
        contextRunner
                .withPropertyValues(
                        "aihub.orcarouter.api-key=sk-orca-test",
                        "aihub.orcarouter.include-cost=true"
                )
                .run(context -> {
                    AiService aiService = context.getBean(AiService.class);
                    Assert.assertEquals("sk-orca-test", aiService.getConfiguration().getOrcaRouterConfig().getApiKey());
                    Assert.assertEquals("https://api.orcarouter.ai/", aiService.getConfiguration().getOrcaRouterConfig().getApiHost());
                    Assert.assertTrue(aiService.getConfiguration().getOrcaRouterConfig().isIncludeCost());
                    Assert.assertTrue(aiService.getChatService(PlatformType.ORCAROUTER) instanceof OpenAiChatService);
                });
    }

    @Test
    public void starterShouldBindGrokAndGeminiNamedEntries() {
        contextRunner
                .withPropertyValues(
                        "aihub.grok.api-key=xai-test",
                        "aihub.gemini.api-key=gemini-test"
                )
                .run(context -> {
                    AiService aiService = context.getBean(AiService.class);
                    Assert.assertEquals("xai-test", aiService.getConfiguration().getGrokConfig().getApiKey());
                    Assert.assertEquals("https://api.x.ai/", aiService.getConfiguration().getGrokConfig().getApiHost());
                    Assert.assertTrue(aiService.getChatService(PlatformType.GROK) instanceof OpenAiChatService);
                    Assert.assertEquals("gemini-test", aiService.getConfiguration().getGeminiConfig().getApiKey());
                    Assert.assertEquals("https://generativelanguage.googleapis.com/v1beta/openai/",
                            aiService.getConfiguration().getGeminiConfig().getApiHost());
                    Assert.assertTrue(aiService.getChatService(PlatformType.GEMINI) instanceof OpenAiChatService);
                });
    }

}
