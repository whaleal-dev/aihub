package com.whaleal.aihub;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author cly
 * @Description Moonshot配置文件
 * @Date 2024/8/30 15:56
 */

@Data
@ConfigurationProperties(prefix = "aihub.moonshot")
public class MoonshotConfigProperties {
    private String apiHost = "https://api.moonshot.cn/";
    private String apiKey = "";
    private String chatCompletionUrl = "v1/chat/completions";
}
