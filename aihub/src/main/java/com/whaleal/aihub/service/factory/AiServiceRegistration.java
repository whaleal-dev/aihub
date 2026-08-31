package com.whaleal.aihub.service.factory;

import com.whaleal.aihub.service.PlatformType;

/**
 * 多实例注册表中的单个注册项。
 */
public class AiServiceRegistration {

    private final String id;
    private final PlatformType platformType;
    private final AiService aiService;

    public AiServiceRegistration(String id, PlatformType platformType, AiService aiService) {
        this.id = id;
        this.platformType = platformType;
        this.aiService = aiService;
    }

    public String getId() {
        return id;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public AiService getAiService() {
        return aiService;
    }
}

