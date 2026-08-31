package com.whaleal.aihub.service.factory;

import com.whaleal.aihub.config.AiPlatform;
import com.whaleal.aihub.config.BaichuanConfig;
import com.whaleal.aihub.config.DashScopeConfig;
import com.whaleal.aihub.config.DeepSeekConfig;
import com.whaleal.aihub.config.DoubaoConfig;
import com.whaleal.aihub.config.GeminiConfig;
import com.whaleal.aihub.config.GrokConfig;
import com.whaleal.aihub.config.HunyuanConfig;
import com.whaleal.aihub.config.JinaConfig;
import com.whaleal.aihub.config.LingyiConfig;
import com.whaleal.aihub.config.MinimaxConfig;
import com.whaleal.aihub.config.MoonshotConfig;
import com.whaleal.aihub.config.OllamaConfig;
import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.config.OrcaRouterConfig;
import com.whaleal.aihub.config.SunoConfig;
import com.whaleal.aihub.config.ZhipuConfig;
import com.whaleal.aihub.convert.BeanCopy;
import com.whaleal.aihub.service.AiConfig;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.PlatformType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 默认的多实例 {@link AiService} 注册表实现。
 *
 * @author 恒哥
 */
public class DefaultAiServiceRegistry implements AiServiceRegistry {

    private final Map<String, AiServiceRegistration> registrations;

    public DefaultAiServiceRegistry(Map<String, AiServiceRegistration> registrations) {
        this.registrations = Collections.unmodifiableMap(new LinkedHashMap<String, AiServiceRegistration>(registrations));
    }

    public static DefaultAiServiceRegistry empty() {
        return new DefaultAiServiceRegistry(Collections.<String, AiServiceRegistration>emptyMap());
    }

    public static DefaultAiServiceRegistry from(Configuration configuration, AiConfig aiConfig) {
        return from(configuration, aiConfig, new DefaultAiServiceFactory());
    }

    public static DefaultAiServiceRegistry from(Configuration configuration, AiConfig aiConfig, AiServiceFactory aiServiceFactory) {
        if (configuration == null || aiConfig == null || ObjectUtils.isEmpty(aiConfig.getPlatforms())) {
            return empty();
        }

        Map<String, AiServiceRegistration> registrations = new LinkedHashMap<String, AiServiceRegistration>();
        for (AiPlatform aiPlatform : aiConfig.getPlatforms()) {
            if (aiPlatform == null) {
                continue;
            }
            String id = aiPlatform.getId();
            if (StringUtils.isBlank(id)) {
                throw new IllegalArgumentException("Ai platform id must not be blank");
            }
            PlatformType platformType = resolvePlatformType(aiPlatform.getPlatform(), id);
            Configuration scopedConfiguration = createScopedConfiguration(configuration, aiPlatform, platformType);
            registrations.put(id, new AiServiceRegistration(id, platformType, aiServiceFactory.create(scopedConfiguration)));
        }
        return new DefaultAiServiceRegistry(registrations);
    }

    @Override
    public AiServiceRegistration find(String id) {
        return registrations.get(id);
    }

    @Override
    public Set<String> ids() {
        return Collections.unmodifiableSet(new LinkedHashSet<String>(registrations.keySet()));
    }

    private static Configuration createScopedConfiguration(Configuration source, AiPlatform aiPlatform, PlatformType platformType) {
        Configuration target = new Configuration();
        BeanCopy.copyProperties(source, target);
        applyPlatformConfig(target, aiPlatform, platformType);
        return target;
    }

    private static PlatformType resolvePlatformType(String rawPlatform, String id) {
        if (StringUtils.isBlank(rawPlatform)) {
            throw new IllegalArgumentException("Ai platform '" + id + "' platform must not be blank");
        }

        String target = rawPlatform.trim();
        for (PlatformType platformType : PlatformType.values()) {
            if (platformType.getPlatform().equalsIgnoreCase(target)) {
                return platformType;
            }
        }
        throw new IllegalArgumentException("Unsupported ai platform '" + rawPlatform + "' for id '" + id + "'");
    }

    private static void applyPlatformConfig(Configuration target, AiPlatform aiPlatform, PlatformType platformType) {
        switch (platformType) {
            case OPENAI:
                target.setOpenAiConfig(copy(aiPlatform, OpenAiConfig.class));
                break;
            case ZHIPU:
                target.setZhipuConfig(copy(aiPlatform, ZhipuConfig.class));
                break;
            case DEEPSEEK:
                target.setDeepSeekConfig(copy(aiPlatform, DeepSeekConfig.class));
                break;
            case MOONSHOT:
                target.setMoonshotConfig(copy(aiPlatform, MoonshotConfig.class));
                break;
            case HUNYUAN:
                target.setHunyuanConfig(copy(aiPlatform, HunyuanConfig.class));
                break;
            case LINGYI:
                target.setLingyiConfig(copy(aiPlatform, LingyiConfig.class));
                break;
            case OLLAMA:
                target.setOllamaConfig(copy(aiPlatform, OllamaConfig.class));
                break;
            case MINIMAX:
                target.setMinimaxConfig(copy(aiPlatform, MinimaxConfig.class));
                break;
            case BAICHUAN:
                target.setBaichuanConfig(copy(aiPlatform, BaichuanConfig.class));
                break;
            case DASHSCOPE:
                target.setDashScopeConfig(copy(aiPlatform, DashScopeConfig.class));
                break;
            case DOUBAO:
                target.setDoubaoConfig(copy(aiPlatform, DoubaoConfig.class));
                break;
            case JINA:
                target.setJinaConfig(copy(aiPlatform, JinaConfig.class));
                break;
            case SUNO:
                target.setSunoConfig(copy(aiPlatform, SunoConfig.class));
                break;
            case GROK:
                target.setGrokConfig(copy(aiPlatform, GrokConfig.class));
                break;
            case GEMINI:
                target.setGeminiConfig(copy(aiPlatform, GeminiConfig.class));
                break;
            case ORCAROUTER:
                target.setOrcaRouterConfig(copy(aiPlatform, OrcaRouterConfig.class));
                break;
            default:
                throw new IllegalArgumentException("Unsupported platform type: " + platformType);
        }
    }

    private static <T> T copy(AiPlatform aiPlatform, Class<T> type) {
        try {
            T target = type.getDeclaredConstructor().newInstance();
            BeanCopy.copyPropertiesIgnoreNull(aiPlatform, target);
            return target;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot instantiate config type: " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot access config type: " + type.getName(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Cannot invoke config constructor: " + type.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Missing no-args constructor for config type: " + type.getName(), e);
        }
    }
}

