package com.whaleal.aihub.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Google Gemini 配置。走官方 OpenAI 兼容层，不另写 generateContent 协议。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiConfig {

    private String apiHost = "https://generativelanguage.googleapis.com/v1beta/openai/";
    private String apiKey = "";
    private String chatCompletionUrl = "chat/completions";
    private String embeddingUrl = "embeddings";
    private String imageGenerationUrl = "images/generations";

    public OpenAiConfig toOpenAiConfig() {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        openAiConfig.setApiHost(apiHost);
        openAiConfig.setApiKey(apiKey);
        openAiConfig.setChatCompletionUrl(chatCompletionUrl);
        openAiConfig.setEmbeddingUrl(embeddingUrl);
        openAiConfig.setImageGenerationUrl(imageGenerationUrl);
        return openAiConfig;
    }
}
