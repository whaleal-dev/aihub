package com.whaleal.aihub;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 配置示例：
 * <pre>
 * ai:
 *   platforms:
 *     - id: "aliyun"
 *       platform: "openai"
 *       api-key: "sk-xxx"
 *       api-host: "https://dashscope.aliyuncs.com/compatible-mode/"
 *     - id: "baidu"
 *       platform: "openai"
 *       api-key: "sk-xxx"
 *       api-host: "https://dashscope.aliyuncs.com/compatible-mode/"
 * </pre>
 */
@ConfigurationProperties(prefix = "aihub")
@Data
public class AiConfigProperties {

    private List<AiPlatformProperties> platforms;
}
