package com.whaleal.aihub;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aihub.jina")
public class JinaConfigProperties {

    private String apiHost = "https://api.jina.ai/";

    private String apiKey = "";

    private String rerankUrl = "v1/rerank";
}
