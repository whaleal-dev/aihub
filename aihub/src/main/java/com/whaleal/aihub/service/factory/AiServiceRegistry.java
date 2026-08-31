package com.whaleal.aihub.service.factory;

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
 * 按 id 管理多套 {@link AiService} 的正式抽象。
 *
 * @author 恒哥
 */
public interface AiServiceRegistry {

    AiServiceRegistration find(String id);

    Set<String> ids();

    default boolean contains(String id) {
        return find(id) != null;
    }

    default AiServiceRegistration get(String id) {
        AiServiceRegistration registration = find(id);
        if (registration == null) {
            throw new IllegalArgumentException("Unknown ai service id: " + id);
        }
        return registration;
    }

    default AiService getAiService(String id) {
        return get(id).getAiService();
    }

    default IChatService getChatService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getChatService(registration.getPlatformType());
    }

    default IEmbeddingService getEmbeddingService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getEmbeddingService(registration.getPlatformType());
    }

    default IAudioService getAudioService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getAudioService(registration.getPlatformType());
    }

    default IRealtimeService getRealtimeService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getRealtimeService(registration.getPlatformType());
    }

    default IImageService getImageService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getImageService(registration.getPlatformType());
    }

    default IVideoService getVideoService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getVideoService(registration.getPlatformType());
    }

    default IMusicService getMusicService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getMusicService(registration.getPlatformType());
    }

    default IResponsesService getResponsesService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getResponsesService(registration.getPlatformType());
    }

    default IRerankService getRerankService(String id) {
        AiServiceRegistration registration = get(id);
        return registration.getAiService().getRerankService(registration.getPlatformType());
    }
}
