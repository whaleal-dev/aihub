package com.whaleal.aihub.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrcaRouter 聚合网关配置。协议复用 OpenAI 兼容栈，这里只提供默认 host 与可选费用头。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcaRouterConfig {

    private String apiHost = "https://api.orcarouter.ai/";
    private String apiKey = "";
    private String chatCompletionUrl = "v1/chat/completions";
    private String embeddingUrl = "v1/embeddings";
    private String speechUrl = "v1/audio/speech";
    private String transcriptionUrl = "v1/audio/transcriptions";
    private String translationUrl = "v1/audio/translations";
    private String imageGenerationUrl = "v1/images/generations";
    private String responsesUrl = "v1/responses";
    /**
     * 为 true 时在请求上附加 {@code X-OrcaRouter-Include-Cost: true}，响应 usage 里会带 {@code cost_usd}。
     */
    private boolean includeCost;

    public OpenAiConfig toOpenAiConfig() {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        openAiConfig.setApiHost(apiHost);
        openAiConfig.setApiKey(apiKey);
        openAiConfig.setChatCompletionUrl(chatCompletionUrl);
        openAiConfig.setEmbeddingUrl(embeddingUrl);
        openAiConfig.setSpeechUrl(speechUrl);
        openAiConfig.setTranscriptionUrl(transcriptionUrl);
        openAiConfig.setTranslationUrl(translationUrl);
        openAiConfig.setImageGenerationUrl(imageGenerationUrl);
        openAiConfig.setResponsesUrl(responsesUrl);
        return openAiConfig;
    }
}
