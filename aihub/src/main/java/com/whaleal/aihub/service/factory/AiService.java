package com.whaleal.aihub.service.factory;

import com.whaleal.aihub.config.GeminiConfig;
import com.whaleal.aihub.config.GrokConfig;
import com.whaleal.aihub.config.OrcaRouterConfig;
import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.interceptor.OrcaRouterCostHeaderInterceptor;
import com.whaleal.aihub.platform.anthropic.chat.AnthropicChatService;
import com.whaleal.aihub.platform.anthropic.chat.AnthropicMessagesService;
import com.whaleal.aihub.platform.baichuan.chat.BaichuanChatService;
import com.whaleal.aihub.platform.dashscope.DashScopeChatService;
import com.whaleal.aihub.platform.dashscope.response.DashScopeResponsesService;
import com.whaleal.aihub.platform.deepseek.chat.DeepSeekChatService;
import com.whaleal.aihub.platform.doubao.chat.DoubaoChatService;
import com.whaleal.aihub.platform.doubao.image.DoubaoImageService;
import com.whaleal.aihub.platform.doubao.rerank.DoubaoRerankService;
import com.whaleal.aihub.platform.doubao.response.DoubaoResponsesService;
import com.whaleal.aihub.platform.doubao.video.SeedanceVideoService;
import com.whaleal.aihub.platform.grok.video.GrokVideoService;
import com.whaleal.aihub.platform.hunyuan.chat.HunyuanChatService;
import com.whaleal.aihub.platform.jina.rerank.JinaRerankService;
import com.whaleal.aihub.platform.lingyi.chat.LingyiChatService;
import com.whaleal.aihub.platform.minimax.chat.MinimaxChatService;
import com.whaleal.aihub.platform.minimax.video.MinimaxVideoService;
import com.whaleal.aihub.platform.moonshot.chat.MoonshotChatService;
import com.whaleal.aihub.platform.ollama.chat.OllamaAiChatService;
import com.whaleal.aihub.platform.ollama.embedding.OllamaEmbeddingService;
import com.whaleal.aihub.platform.ollama.rerank.OllamaRerankService;
import com.whaleal.aihub.platform.openai.audio.OpenAiAudioService;
import com.whaleal.aihub.platform.openai.chat.OpenAiChatService;
import com.whaleal.aihub.platform.openai.embedding.OpenAiEmbeddingService;
import com.whaleal.aihub.platform.openai.image.OpenAiImageService;
import com.whaleal.aihub.platform.openai.realtime.OpenAiRealtimeService;
import com.whaleal.aihub.platform.openai.response.OpenAiResponsesService;
import com.whaleal.aihub.platform.openai.video.OpenAiVideoService;
import com.whaleal.aihub.platform.suno.music.SunoMusicService;
import com.whaleal.aihub.platform.zhipu.chat.ZhipuChatService;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IAudioService;
import com.whaleal.aihub.service.IChatService;
import com.whaleal.aihub.service.IEmbeddingService;
import com.whaleal.aihub.service.IImageService;
import com.whaleal.aihub.service.IMessagesService;
import com.whaleal.aihub.service.IMusicService;
import com.whaleal.aihub.service.IRealtimeService;
import com.whaleal.aihub.service.IRerankService;
import com.whaleal.aihub.service.IResponsesService;
import com.whaleal.aihub.service.IVideoService;
import com.whaleal.aihub.service.PlatformType;
import okhttp3.OkHttpClient;

/**
 * 模型客户端工厂：按平台创建 Chat / Embedding / 媒体等 API 封装。
 *
 * @Author cly
 * @author 恒哥
 */
public class AiService {

    private final Configuration configuration;

    public AiService(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public IChatService getChatService(PlatformType platform) {
        return createChatService(platform);
    }

    private IChatService createChatService(PlatformType platform) {
        switch (platform) {
            case OPENAI:
                return new OpenAiChatService(configuration);
            case ANTHROPIC:
                return new AnthropicChatService(configuration);
            case ZHIPU:
                return new ZhipuChatService(configuration);
            case DEEPSEEK:
                return new DeepSeekChatService(configuration);
            case MOONSHOT:
                return new MoonshotChatService(configuration);
            case HUNYUAN:
                return new HunyuanChatService(configuration);
            case LINGYI:
                return new LingyiChatService(configuration);
            case OLLAMA:
                return new OllamaAiChatService(configuration);
            case MINIMAX:
                return new MinimaxChatService(configuration);
            case BAICHUAN:
                return new BaichuanChatService(configuration);
            case DASHSCOPE:
                return new DashScopeChatService(configuration);
            case DOUBAO:
                return new DoubaoChatService(configuration);
            case GROK:
                return new OpenAiChatService(grokScope());
            case GEMINI:
                return new OpenAiChatService(geminiScope());
            case ORCAROUTER:
                return new OpenAiChatService(orcaRouterScope());
            default:
                throw new IllegalArgumentException("Unknown platform: " + platform);
        }
    }

    public IMessagesService getMessagesService(PlatformType platform) {
        if (platform == PlatformType.ANTHROPIC) {
            return new AnthropicMessagesService(configuration);
        }
        throw new IllegalArgumentException("No native Messages service for platform: " + platform);
    }

    public IEmbeddingService getEmbeddingService(PlatformType platform) {
        switch (platform) {
            case OPENAI:
                return new OpenAiEmbeddingService(configuration);
            case GROK:
                return new OpenAiEmbeddingService(grokScope());
            case GEMINI:
                return new OpenAiEmbeddingService(geminiScope());
            case ORCAROUTER:
                return new OpenAiEmbeddingService(orcaRouterScope());
            case OLLAMA:
                return new OllamaEmbeddingService(configuration);
            default:
                throw new IllegalArgumentException("Unknown platform: " + platform);
        }
    }

    public IAudioService getAudioService(PlatformType platform) {
        if (platform == PlatformType.OPENAI) {
            return new OpenAiAudioService(configuration);
        }
        if (platform == PlatformType.ORCAROUTER) {
            return new OpenAiAudioService(orcaRouterScope());
        }
        throw new IllegalArgumentException("Unknown platform: " + platform);
    }

    public IRealtimeService getRealtimeService(PlatformType platform) {
        if (platform == PlatformType.OPENAI) {
            return new OpenAiRealtimeService(configuration);
        }
        throw new IllegalArgumentException("Unknown platform: " + platform);
    }

    public IImageService getImageService(PlatformType platform) {
        switch (platform) {
            case OPENAI:
                return new OpenAiImageService(configuration);
            case GROK:
                return new OpenAiImageService(grokScope());
            case GEMINI:
                return new OpenAiImageService(geminiScope());
            case ORCAROUTER:
                return new OpenAiImageService(orcaRouterScope());
            case DOUBAO:
                return new DoubaoImageService(configuration);
            default:
                throw new IllegalArgumentException("Unknown platform: " + platform);
        }
    }

    public IVideoService getVideoService(PlatformType platform) {
        switch (platform) {
            case OPENAI:
                return new OpenAiVideoService(configuration);
            case GROK:
                return new GrokVideoService(grokScope());
            case MINIMAX:
                return new MinimaxVideoService(configuration);
            case DOUBAO:
                return new SeedanceVideoService(configuration);
            default:
                throw new IllegalArgumentException("No video service for platform: " + platform);
        }
    }

    public IMusicService getMusicService(PlatformType platform) {
        if (platform == PlatformType.SUNO) {
            return new SunoMusicService(configuration);
        }
        throw new IllegalArgumentException("No music service for platform: " + platform);
    }

    public IResponsesService getResponsesService(PlatformType platform) {
        switch (platform) {
            case OPENAI:
                return new OpenAiResponsesService(configuration);
            case ORCAROUTER:
                return new OpenAiResponsesService(orcaRouterScope());
            case DOUBAO:
                return new DoubaoResponsesService(configuration);
            case DASHSCOPE:
                return new DashScopeResponsesService(configuration);
            default:
                throw new IllegalArgumentException("Unknown platform: " + platform);
        }
    }

    public IRerankService getRerankService(PlatformType platform) {
        switch (platform) {
            case JINA:
                return new JinaRerankService(configuration);
            case OLLAMA:
                return new OllamaRerankService(configuration);
            case DOUBAO:
                return new DoubaoRerankService(configuration);
            default:
                throw new IllegalArgumentException("Unknown platform: " + platform);
        }
    }

    /**
     * OrcaRouter 是 OpenAI 兼容网关：把其 host/key 投影成 {@link com.whaleal.aihub.config.OpenAiConfig}，
     * 可选再挂费用头拦截器。不改动调用方共享的 {@link Configuration}。
     */
    private Configuration orcaRouterScope() {
        OrcaRouterConfig orcaRouterConfig = configuration.getOrcaRouterConfig();
        if (orcaRouterConfig == null) {
            throw new IllegalStateException("OrcaRouterConfig is required for PlatformType.ORCAROUTER");
        }
        Configuration scoped = new Configuration();
        OkHttpClient client = configuration.getOkHttpClient();
        if (orcaRouterConfig.isIncludeCost() && client != null) {
            client = client.newBuilder()
                    .addInterceptor(new OrcaRouterCostHeaderInterceptor())
                    .build();
        }
        scoped.setOkHttpClient(client);
        scoped.setOpenAiConfig(orcaRouterConfig.toOpenAiConfig());
        scoped.setOrcaRouterConfig(orcaRouterConfig);
        return scoped;
    }

    /**
     * Grok / xAI 是 OpenAI 兼容入口。优先用 {@link GrokConfig}；若没有则回退到已有 {@code OpenAiConfig}，
     * 兼容原先只配 OpenAI host 打 Grok 视频的用法。
     */
    private Configuration grokScope() {
        GrokConfig grokConfig = configuration.getGrokConfig();
        if (grokConfig != null) {
            Configuration scoped = projectOpenAi(grokConfig.toOpenAiConfig());
            scoped.setGrokConfig(grokConfig);
            return scoped;
        }
        if (configuration.getOpenAiConfig() != null) {
            return configuration;
        }
        throw new IllegalStateException("GrokConfig is required for PlatformType.GROK");
    }

    /**
     * Gemini 走官方 OpenAI 兼容层（{@code /v1beta/openai/chat/completions}），不另写 generateContent。
     */
    private Configuration geminiScope() {
        GeminiConfig geminiConfig = configuration.getGeminiConfig();
        if (geminiConfig == null) {
            throw new IllegalStateException("GeminiConfig is required for PlatformType.GEMINI");
        }
        Configuration scoped = projectOpenAi(geminiConfig.toOpenAiConfig());
        scoped.setGeminiConfig(geminiConfig);
        return scoped;
    }

    private Configuration projectOpenAi(OpenAiConfig openAiConfig) {
        Configuration scoped = new Configuration();
        scoped.setOkHttpClient(configuration.getOkHttpClient());
        scoped.setOpenAiConfig(openAiConfig);
        return scoped;
    }
}
