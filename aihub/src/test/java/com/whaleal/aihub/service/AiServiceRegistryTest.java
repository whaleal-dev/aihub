package com.whaleal.aihub.service;

import com.whaleal.aihub.config.AiPlatform;
import com.whaleal.aihub.config.JinaConfig;
import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.config.SunoConfig;
import com.whaleal.aihub.platform.jina.rerank.JinaRerankService;
import com.whaleal.aihub.platform.openai.chat.OpenAiChatService;
import com.whaleal.aihub.platform.openai.video.OpenAiVideoService;
import com.whaleal.aihub.platform.suno.music.SunoMusicService;
import com.whaleal.aihub.service.AiConfig;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.service.factory.AiServiceRegistration;
import com.whaleal.aihub.service.factory.AiServiceRegistry;
import com.whaleal.aihub.service.factory.DefaultAiServiceRegistry;
import com.whaleal.aihub.service.factory.FreeAiService;
import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class AiServiceRegistryTest {

    @Test
    public void shouldBuildRegistryFromConfiguredPlatforms() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-a-openai");
        aiPlatform.setPlatform("openai");
        aiPlatform.setApiHost("https://example-openai.local/");
        aiPlatform.setApiKey("sk-test");
        aiPlatform.setImageGenerationUrl("v1/images/generations");
        aiPlatform.setResponsesUrl("v1/responses");
        aiPlatform.setVideoUrl("v1/videos");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        AiServiceRegistry registry = DefaultAiServiceRegistry.from(configuration, aiConfig);
        AiServiceRegistration registration = registry.get("tenant-a-openai");
        AiService aiService = registration.getAiService();

        Assert.assertTrue(registry.contains("tenant-a-openai"));
        Assert.assertEquals(PlatformType.OPENAI, registration.getPlatformType());
        Assert.assertTrue(registry.getChatService("tenant-a-openai") instanceof OpenAiChatService);
        Assert.assertTrue(registry.getVideoService("tenant-a-openai") instanceof OpenAiVideoService);
        Assert.assertNotNull(aiService);
        Assert.assertNotNull(aiService.getConfiguration());
        Assert.assertNotSame(configuration, aiService.getConfiguration());

        OpenAiConfig scopedOpenAiConfig = aiService.getConfiguration().getOpenAiConfig();
        Assert.assertEquals("https://example-openai.local/", scopedOpenAiConfig.getApiHost());
        Assert.assertEquals("sk-test", scopedOpenAiConfig.getApiKey());
        Assert.assertEquals("v1/images/generations", scopedOpenAiConfig.getImageGenerationUrl());
        Assert.assertEquals("v1/responses", scopedOpenAiConfig.getResponsesUrl());
        Assert.assertEquals("v1/videos", scopedOpenAiConfig.getVideoUrl());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldKeepFreeAiServiceAsCompatibilityShell() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-a-openai");
        aiPlatform.setPlatform("openai");
        aiPlatform.setApiHost("https://example-openai.local/");
        aiPlatform.setApiKey("sk-test");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        new FreeAiService(configuration, aiConfig);

        Assert.assertTrue(FreeAiService.contains("tenant-a-openai"));
        Assert.assertTrue(FreeAiService.getChatService("tenant-a-openai") instanceof OpenAiChatService);
        Assert.assertTrue(FreeAiService.getVideoService("tenant-a-openai") instanceof OpenAiVideoService);
        Assert.assertNull(FreeAiService.getChatService("missing"));
    }


    @Test
    @SuppressWarnings("deprecation")
    public void shouldExposeSunoMusicServiceFromRegistryAndCompatibilityShell() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-suno");
        aiPlatform.setPlatform("suno");
        aiPlatform.setApiHost("https://api.chatfire.cn/");
        aiPlatform.setApiKey("suno-key");
        aiPlatform.setMusicUrl("suno/submit/music");
        aiPlatform.setLyricsUrl("suno/submit/lyrics");
        aiPlatform.setFetchUrl("suno/fetch/{task_id}");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        AiServiceRegistry registry = DefaultAiServiceRegistry.from(configuration, aiConfig);
        AiServiceRegistration registration = registry.get("tenant-suno");
        AiService aiService = registration.getAiService();

        Assert.assertEquals(PlatformType.SUNO, registration.getPlatformType());
        Assert.assertTrue(registry.getMusicService("tenant-suno") instanceof SunoMusicService);

        SunoConfig scopedSunoConfig = aiService.getConfiguration().getSunoConfig();
        Assert.assertEquals("https://api.chatfire.cn/", scopedSunoConfig.getApiHost());
        Assert.assertEquals("suno-key", scopedSunoConfig.getApiKey());
        Assert.assertEquals("suno/submit/music", scopedSunoConfig.getMusicUrl());
        Assert.assertEquals("suno/submit/lyrics", scopedSunoConfig.getLyricsUrl());
        Assert.assertEquals("suno/fetch/{task_id}", scopedSunoConfig.getFetchUrl());

        new FreeAiService(registry);
        Assert.assertTrue(FreeAiService.getMusicService("tenant-suno") instanceof SunoMusicService);
    }

    @Test
    public void shouldFailFastWhenPlatformIsUnsupported() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-a-unknown");
        aiPlatform.setPlatform("unknown-provider");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        try {
            DefaultAiServiceRegistry.from(configuration, aiConfig);
            Assert.fail("Expected unsupported platform to fail fast");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Unsupported ai platform 'unknown-provider' for id 'tenant-a-unknown'", e.getMessage());
        }
    }

    @Test
    public void shouldExposeJinaCompatibleRerankServiceFromRegistry() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-a-rerank");
        aiPlatform.setPlatform("jina");
        aiPlatform.setApiHost("https://api.jina.ai/");
        aiPlatform.setApiKey("jina-key");
        aiPlatform.setRerankUrl("v1/rerank");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        AiServiceRegistry registry = DefaultAiServiceRegistry.from(configuration, aiConfig);
        AiServiceRegistration registration = registry.get("tenant-a-rerank");

        Assert.assertEquals(PlatformType.JINA, registration.getPlatformType());
        Assert.assertTrue(registry.getRerankService("tenant-a-rerank") instanceof JinaRerankService);
        JinaConfig scopedJinaConfig = registration.getAiService().getConfiguration().getJinaConfig();
        Assert.assertEquals("https://api.jina.ai/", scopedJinaConfig.getApiHost());
        Assert.assertEquals("jina-key", scopedJinaConfig.getApiKey());
        Assert.assertEquals("v1/rerank", scopedJinaConfig.getRerankUrl());

    }

    @Test
    public void shouldExposeOrcaRouterAsOpenAiCompatibleStack() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-orca");
        aiPlatform.setPlatform("orcarouter");
        aiPlatform.setApiKey("sk-orca-test");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        AiServiceRegistry registry = DefaultAiServiceRegistry.from(configuration, aiConfig);
        AiServiceRegistration registration = registry.get("tenant-orca");
        AiService aiService = registration.getAiService();

        Assert.assertEquals(PlatformType.ORCAROUTER, registration.getPlatformType());
        Assert.assertTrue(registry.getChatService("tenant-orca") instanceof OpenAiChatService);
        Assert.assertEquals("https://api.orcarouter.ai/", aiService.getConfiguration().getOrcaRouterConfig().getApiHost());
        Assert.assertEquals("sk-orca-test", aiService.getConfiguration().getOrcaRouterConfig().getApiKey());
    }

    @Test
    public void shouldExposeGrokAsOpenAiCompatibleChatStack() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-grok");
        aiPlatform.setPlatform("grok");
        aiPlatform.setApiKey("xai-test");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        AiServiceRegistry registry = DefaultAiServiceRegistry.from(configuration, aiConfig);
        AiServiceRegistration registration = registry.get("tenant-grok");
        AiService aiService = registration.getAiService();

        Assert.assertEquals(PlatformType.GROK, registration.getPlatformType());
        Assert.assertTrue(registry.getChatService("tenant-grok") instanceof OpenAiChatService);
        Assert.assertEquals("https://api.x.ai/", aiService.getConfiguration().getGrokConfig().getApiHost());
        Assert.assertEquals("xai-test", aiService.getConfiguration().getGrokConfig().getApiKey());
    }

    @Test
    public void shouldExposeGeminiAsOpenAiCompatibleStack() {
        Configuration configuration = new Configuration();
        configuration.setOkHttpClient(new OkHttpClient());

        AiPlatform aiPlatform = new AiPlatform();
        aiPlatform.setId("tenant-gemini");
        aiPlatform.setPlatform("gemini");
        aiPlatform.setApiKey("gemini-test");

        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(Collections.singletonList(aiPlatform));

        AiServiceRegistry registry = DefaultAiServiceRegistry.from(configuration, aiConfig);
        AiServiceRegistration registration = registry.get("tenant-gemini");
        AiService aiService = registration.getAiService();

        Assert.assertEquals(PlatformType.GEMINI, registration.getPlatformType());
        Assert.assertTrue(registry.getChatService("tenant-gemini") instanceof OpenAiChatService);
        Assert.assertEquals("https://generativelanguage.googleapis.com/v1beta/openai/",
                aiService.getConfiguration().getGeminiConfig().getApiHost());
        Assert.assertEquals("gemini-test", aiService.getConfiguration().getGeminiConfig().getApiKey());
    }

}
