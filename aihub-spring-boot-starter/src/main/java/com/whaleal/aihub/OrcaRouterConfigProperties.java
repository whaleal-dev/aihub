package com.whaleal.aihub;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OrcaRouter 聚合网关 Spring 配置，前缀 {@code aihub.orcarouter}。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "aihub.orcarouter")
public class OrcaRouterConfigProperties {

    private String apiHost = "https://api.orcarouter.ai/";
    private String apiKey = "";
    private String chatCompletionUrl = "v1/chat/completions";
    private String embeddingUrl = "v1/embeddings";
    private String speechUrl = "v1/audio/speech";
    private String transcriptionUrl = "v1/audio/transcriptions";
    private String translationUrl = "v1/audio/translations";
    private String imageGenerationUrl = "v1/images/generations";
    private String responsesUrl = "v1/responses";
    private boolean includeCost;
}
