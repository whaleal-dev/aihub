package com.whaleal.aihub;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;



@Data
@ConfigurationProperties(prefix = "aihub.doubao")
public class DoubaoConfigProperties {

    private String apiHost = "https://ark.cn-beijing.volces.com/api/v3/";
    private String apiKey = "";
    private String chatCompletionUrl = "chat/completions";
    private String imageGenerationUrl = "images/generations";
    private String responsesUrl = "responses";
    private String rerankApiHost = "https://api-knowledgebase.mlp.cn-beijing.volces.com/";
    private String rerankUrl = "api/knowledge/service/rerank";
}

