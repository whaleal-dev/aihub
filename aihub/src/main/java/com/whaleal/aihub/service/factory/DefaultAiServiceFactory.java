package com.whaleal.aihub.service.factory;

import com.whaleal.aihub.service.Configuration;

/**
 * 默认的 {@link AiService} 工厂实现。
 */
public class DefaultAiServiceFactory implements AiServiceFactory {

    @Override
    public AiService create(Configuration configuration) {
        return new AiService(configuration);
    }
}

