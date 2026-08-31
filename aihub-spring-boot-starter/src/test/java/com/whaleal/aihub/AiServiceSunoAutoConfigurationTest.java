package com.whaleal.aihub;

import com.whaleal.aihub.platform.suno.music.SunoMusicService;
import com.whaleal.aihub.service.IMusicService;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.service.factory.AiServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class AiServiceSunoAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AiConfigAutoConfiguration.class);

    @Test
    public void starterShouldBindSingleSunoConfig() {
        contextRunner
                .withPropertyValues(
                        "aihub.suno.api-key=suno-key",
                        "aihub.suno.api-host=https://api.chatfire.cn/",
                        "aihub.suno.music-url=suno/submit/music",
                        "aihub.suno.lyrics-url=suno/submit/lyrics",
                        "aihub.suno.fetch-url=suno/fetch/{task_id}"
                )
                .run(context -> {
                    AiService aiService = context.getBean(AiService.class);
                    Assert.assertEquals("suno-key", aiService.getConfiguration().getSunoConfig().getApiKey());
                    Assert.assertEquals("https://api.chatfire.cn/", aiService.getConfiguration().getSunoConfig().getApiHost());
                    Assert.assertEquals("suno/submit/music", aiService.getConfiguration().getSunoConfig().getMusicUrl());
                    Assert.assertEquals("suno/submit/lyrics", aiService.getConfiguration().getSunoConfig().getLyricsUrl());
                    Assert.assertEquals("suno/fetch/{task_id}", aiService.getConfiguration().getSunoConfig().getFetchUrl());

                    IMusicService musicService = aiService.getMusicService(PlatformType.SUNO);
                    Assert.assertTrue(musicService instanceof SunoMusicService);
                });
    }

    @Test
    public void starterMultiInstanceShouldBindSunoMusicService() {
        contextRunner
                .withPropertyValues(
                        "aihub.platforms[0].id=chatfire-suno",
                        "aihub.platforms[0].platform=suno",
                        "aihub.platforms[0].api-key=tenant-suno-key",
                        "aihub.platforms[0].api-host=https://api.chatfire.cn/",
                        "aihub.platforms[0].music-url=suno/submit/music",
                        "aihub.platforms[0].lyrics-url=suno/submit/lyrics",
                        "aihub.platforms[0].fetch-url=suno/fetch/{task_id}"
                )
                .run(context -> {
                    AiServiceRegistry registry = context.getBean(AiServiceRegistry.class);
                    AiService aiService = registry.getAiService("chatfire-suno");

                    Assert.assertEquals("tenant-suno-key", aiService.getConfiguration().getSunoConfig().getApiKey());
                    Assert.assertEquals("https://api.chatfire.cn/", aiService.getConfiguration().getSunoConfig().getApiHost());
                    Assert.assertEquals("suno/submit/music", aiService.getConfiguration().getSunoConfig().getMusicUrl());
                    Assert.assertEquals("suno/submit/lyrics", aiService.getConfiguration().getSunoConfig().getLyricsUrl());
                    Assert.assertEquals("suno/fetch/{task_id}", aiService.getConfiguration().getSunoConfig().getFetchUrl());
                    Assert.assertTrue(registry.getMusicService("chatfire-suno") instanceof SunoMusicService);
                });
    }
}
