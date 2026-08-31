package com.whaleal.aihub;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xAI Grok Spring 配置，前缀 {@code aihub.grok}。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "aihub.grok")
public class GrokConfigProperties {

    private String apiHost = "https://api.x.ai/";
    private String apiKey = "";
    private String chatCompletionUrl = "v1/chat/completions";
    private String embeddingUrl = "v1/embeddings";
    private String imageGenerationUrl = "v1/images/generations";
    private String videoUrl = "v1/videos";
    private String videoCreateUrl = "v1/videos/generations";
}
