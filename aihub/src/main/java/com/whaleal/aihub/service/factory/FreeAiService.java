package com.whaleal.aihub.service.factory;

import com.whaleal.aihub.service.AiConfig;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IAudioService;
import com.whaleal.aihub.service.IChatService;
import com.whaleal.aihub.service.IEmbeddingService;
import com.whaleal.aihub.service.IImageService;
import com.whaleal.aihub.service.IMusicService;
import com.whaleal.aihub.service.IRerankService;
import com.whaleal.aihub.service.IRealtimeService;
import com.whaleal.aihub.service.IResponsesService;
import com.whaleal.aihub.service.IVideoService;

import java.util.Set;

/**
 * 兼容旧版本的多实例聊天入口。
 *
 * <p>主线入口仍然是 {@link AiService}。如果需要正式的多实例管理，请使用
 * {@link AiServiceRegistry}。本类保留旧构造方式和静态获取方式，仅作为兼容壳。</p>
 */
@Deprecated
public class FreeAiService {
    private static volatile AiServiceRegistry registry = DefaultAiServiceRegistry.empty();

    private final Configuration configuration;
    private final AiConfig aiConfig;
    private final AiServiceFactory aiServiceFactory;

    public FreeAiService(Configuration configuration, AiConfig aiConfig) {
        this(configuration, aiConfig, new DefaultAiServiceFactory());
    }

    public FreeAiService(Configuration configuration, AiConfig aiConfig, AiServiceFactory aiServiceFactory) {
        this.configuration = configuration;
        this.aiConfig = aiConfig;
        this.aiServiceFactory = aiServiceFactory;
        init();
    }

    public FreeAiService(AiServiceRegistry registry) {
        this.configuration = null;
        this.aiConfig = null;
        this.aiServiceFactory = null;
        setRegistry(registry);
    }

    public void init() {
        if (configuration == null) {
            return;
        }


        setRegistry(DefaultAiServiceRegistry.from(configuration, aiConfig, aiServiceFactory));
    }

    public static IChatService getChatService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getChatService(id);
    }

    public static AiService getAiService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registration.getAiService();
    }

    public static IEmbeddingService getEmbeddingService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getEmbeddingService(id);
    }

    public static IAudioService getAudioService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getAudioService(id);
    }

    public static IRealtimeService getRealtimeService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getRealtimeService(id);
    }

    public static IImageService getImageService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getImageService(id);
    }

    public static IVideoService getVideoService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getVideoService(id);
    }

    public static IMusicService getMusicService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getMusicService(id);
    }

    public static IResponsesService getResponsesService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getResponsesService(id);
    }

    public static IRerankService getRerankService(String id) {
        AiServiceRegistration registration = registry.find(id);
        return registration == null ? null : registry.getRerankService(id);
    }

    public static boolean contains(String id) {
        return registry.contains(id);
    }

    public static Set<String> ids() {
        return registry.ids();
    }

    public static AiServiceRegistry getRegistry() {
        return registry;
    }

    private static void setRegistry(AiServiceRegistry registry) {
        FreeAiService.registry = registry == null ? DefaultAiServiceRegistry.empty() : registry;
    }
}

