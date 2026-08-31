package com.whaleal.aihub.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author cly
 * @Description TODO
 * @Date 2024/8/8 17:29
 * @author 恒哥
 */
@AllArgsConstructor
@Getter
public enum PlatformType {
    OPENAI("openai"),
    ANTHROPIC("anthropic"),
    ZHIPU("zhipu"),
    DEEPSEEK("deepseek"),
    MOONSHOT("moonshot"),
    HUNYUAN("hunyuan"),
    LINGYI("lingyi"),
    OLLAMA("ollama"),
    MINIMAX("minimax"),
    BAICHUAN("baichuan"),
    DASHSCOPE("dashscope"),
    DOUBAO("doubao"),
    JINA("jina"),
    SUNO("suno"),
    GROK("grok"),
    GEMINI("gemini"),
    ORCAROUTER("orcarouter"),
    ;
    private final String platform;

    public static PlatformType getPlatform(String value) {
        String target = value.toLowerCase();
        for (PlatformType platformType : PlatformType.values()) {
            if (platformType.getPlatform().equals(target)) {
                return platformType;
            }
        }
        return PlatformType.OPENAI;
    }
}
