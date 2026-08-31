package com.whaleal.aihub;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Google Gemini Spring 配置，前缀 {@code aihub.gemini}。默认打官方 OpenAI 兼容层。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "aihub.gemini")
public class GeminiConfigProperties {

    private String apiHost = "https://generativelanguage.googleapis.com/v1beta/openai/";
    private String apiKey = "";
    private String chatCompletionUrl = "chat/completions";
    private String embeddingUrl = "embeddings";
    private String imageGenerationUrl = "images/generations";
}
