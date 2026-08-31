package com.whaleal.aihub.service.factory;

import com.whaleal.aihub.service.Configuration;

/**
 * {@link AiService} 的创建工厂。
 */
public interface AiServiceFactory {

    AiService create(Configuration configuration);
}

