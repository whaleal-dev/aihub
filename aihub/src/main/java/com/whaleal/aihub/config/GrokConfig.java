package com.whaleal.aihub.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * xAI Grok 配置。Chat / Image 走 OpenAI 兼容协议，Video 仍用 {@code GrokVideoService}。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrokConfig {

    private String apiHost = "https://api.x.ai/";
    private String apiKey = "";
    private String chatCompletionUrl = "v1/chat/completions";
    private String embeddingUrl = "v1/embeddings";
    private String imageGenerationUrl = "v1/images/generations";
    private String videoUrl = "v1/videos";
    private String videoCreateUrl = "v1/videos/generations";

    public OpenAiConfig toOpenAiConfig() {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        openAiConfig.setApiHost(apiHost);
        openAiConfig.setApiKey(apiKey);
        openAiConfig.setChatCompletionUrl(chatCompletionUrl);
        openAiConfig.setEmbeddingUrl(embeddingUrl);
        openAiConfig.setImageGenerationUrl(imageGenerationUrl);
        openAiConfig.setVideoUrl(videoUrl);
        openAiConfig.setVideoCreateUrl(videoCreateUrl);
        return openAiConfig;
    }
}
