package com.whaleal.aihub;

import com.whaleal.aihub.config.AiPlatform;
import com.whaleal.aihub.convert.BeanCopy;
import com.whaleal.aihub.config.AnthropicConfig;
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
import com.whaleal.aihub.interceptor.ContentTypeInterceptor;
import com.whaleal.aihub.interceptor.ErrorInterceptor;
import com.whaleal.aihub.network.ConnectionPoolProvider;
import com.whaleal.aihub.network.DispatcherProvider;
import com.whaleal.aihub.network.OkHttpUtil;
import com.whaleal.aihub.service.AiConfig;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.service.factory.AiServiceFactory;
import com.whaleal.aihub.service.factory.AiServiceRegistry;
import com.whaleal.aihub.service.factory.DefaultAiServiceFactory;
import com.whaleal.aihub.service.factory.DefaultAiServiceRegistry;
import com.whaleal.aihub.service.factory.FreeAiService;
import com.whaleal.aihub.service.spi.ServiceLoaderUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author cly
 * @Description TODO
 * @Date 2024/8/9 23:22
 * @author 恒哥
 */
@Configuration
@EnableConfigurationProperties({
        AiConfigProperties.class,
        OpenAiConfigProperties.class,
        OkHttpConfigProperties.class,
        ZhipuConfigProperties.class,
        AnthropicConfigProperties.class,
        DeepSeekConfigProperties.class,
        MoonshotConfigProperties.class,
        HunyuanConfigProperties.class,
        LingyiConfigProperties.class,
        OllamaConfigProperties.class,
        MinimaxConfigProperties.class,
        BaichuanConfigProperties.class,
        DashScopeConfigProperties.class,
        DoubaoConfigProperties.class,
        JinaConfigProperties.class,
        SunoConfigProperties.class,
        GrokConfigProperties.class,
        GeminiConfigProperties.class,
        OrcaRouterConfigProperties.class
})
public class AiConfigAutoConfiguration {

    private final OkHttpConfigProperties okHttpConfigProperties;
    private final AiConfigProperties aiConfigProperties;
    private final OpenAiConfigProperties openAiConfigProperties;
    private final ZhipuConfigProperties zhipuConfigProperties;
    private final AnthropicConfigProperties anthropicConfigProperties;
    private final DeepSeekConfigProperties deepSeekConfigProperties;
    private final MoonshotConfigProperties moonshotConfigProperties;
    private final HunyuanConfigProperties hunyuanConfigProperties;
    private final LingyiConfigProperties lingyiConfigProperties;
    private final OllamaConfigProperties ollamaConfigProperties;
    private final MinimaxConfigProperties minimaxConfigProperties;
    private final BaichuanConfigProperties baichuanConfigProperties;
    private final DashScopeConfigProperties dashScopeConfigProperties;
    private final DoubaoConfigProperties doubaoConfigProperties;
    private final JinaConfigProperties jinaConfigProperties;
    private final SunoConfigProperties sunoConfigProperties;
    private final GrokConfigProperties grokConfigProperties;
    private final GeminiConfigProperties geminiConfigProperties;
    private final OrcaRouterConfigProperties orcaRouterConfigProperties;

    private com.whaleal.aihub.service.Configuration configuration = new com.whaleal.aihub.service.Configuration();

    public AiConfigAutoConfiguration(OkHttpConfigProperties okHttpConfigProperties,
                                     OpenAiConfigProperties openAiConfigProperties,
                                     AiConfigProperties aiConfigProperties,
                                     ZhipuConfigProperties zhipuConfigProperties,
                                     AnthropicConfigProperties anthropicConfigProperties,
                                     DeepSeekConfigProperties deepSeekConfigProperties,
                                     MoonshotConfigProperties moonshotConfigProperties,
                                     HunyuanConfigProperties hunyuanConfigProperties,
                                     LingyiConfigProperties lingyiConfigProperties,
                                     OllamaConfigProperties ollamaConfigProperties,
                                     MinimaxConfigProperties minimaxConfigProperties,
                                     BaichuanConfigProperties baichuanConfigProperties,
                                     DashScopeConfigProperties dashScopeConfigProperties,
                                     DoubaoConfigProperties doubaoConfigProperties,
                                     JinaConfigProperties jinaConfigProperties,
                                     SunoConfigProperties sunoConfigProperties,
                                     GrokConfigProperties grokConfigProperties,
                                     GeminiConfigProperties geminiConfigProperties,
                                     OrcaRouterConfigProperties orcaRouterConfigProperties) {
        this.okHttpConfigProperties = okHttpConfigProperties;
        this.openAiConfigProperties = openAiConfigProperties;
        this.aiConfigProperties = aiConfigProperties;
        this.zhipuConfigProperties = zhipuConfigProperties;
        this.anthropicConfigProperties = anthropicConfigProperties;
        this.deepSeekConfigProperties = deepSeekConfigProperties;
        this.moonshotConfigProperties = moonshotConfigProperties;
        this.hunyuanConfigProperties = hunyuanConfigProperties;
        this.lingyiConfigProperties = lingyiConfigProperties;
        this.ollamaConfigProperties = ollamaConfigProperties;
        this.minimaxConfigProperties = minimaxConfigProperties;
        this.baichuanConfigProperties = baichuanConfigProperties;
        this.dashScopeConfigProperties = dashScopeConfigProperties;
        this.doubaoConfigProperties = doubaoConfigProperties;
        this.jinaConfigProperties = jinaConfigProperties;
        this.sunoConfigProperties = sunoConfigProperties;
        this.grokConfigProperties = grokConfigProperties;
        this.geminiConfigProperties = geminiConfigProperties;
        this.orcaRouterConfigProperties = orcaRouterConfigProperties;
    }

    @Bean
    public AiService aiService() {
        return new AiService(configuration);
    }

    @Bean
    public AiServiceFactory aiServiceFactory() {
        return new DefaultAiServiceFactory();
    }

    @Bean
    public AiServiceRegistry aiServiceRegistry(AiServiceFactory aiServiceFactory) {
        AiConfig aiConfig = new AiConfig();
        aiConfig.setPlatforms(BeanCopy.copyToList(aiConfigProperties.getPlatforms(), AiPlatform.class));
        return DefaultAiServiceRegistry.from(configuration, aiConfig, aiServiceFactory);
    }

    @Bean
    public FreeAiService getFreeAiService(AiServiceRegistry aiServiceRegistry) {
        return new FreeAiService(aiServiceRegistry);
    }

    @PostConstruct
    private void init() {
        initOkHttp();
        initOpenAiConfig();
        initZhipuConfig();
        initAnthropicConfig();
        initDeepSeekConfig();
        initMoonshotConfig();
        initHunyuanConfig();
        initLingyiConfig();
        initOllamaConfig();
        initMinimaxConfig();
        initBaichuanConfig();
        initDashScopeConfig();
        initDoubaoConfig();
        initJinaConfig();
        initSunoConfig();
        initGrokConfig();
        initGeminiConfig();
        initOrcaRouterConfig();
    }

    private void initOkHttp() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(okHttpConfigProperties.getLog());

        DispatcherProvider dispatcherProvider = ServiceLoaderUtil.load(DispatcherProvider.class);
        ConnectionPoolProvider connectionPoolProvider = ServiceLoaderUtil.load(ConnectionPoolProvider.class);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new ErrorInterceptor())
                .addInterceptor(new ContentTypeInterceptor())
                .connectTimeout(okHttpConfigProperties.getConnectTimeout(), okHttpConfigProperties.getTimeUnit())
                .writeTimeout(okHttpConfigProperties.getWriteTimeout(), okHttpConfigProperties.getTimeUnit())
                .readTimeout(okHttpConfigProperties.getReadTimeout(), okHttpConfigProperties.getTimeUnit())
                .dispatcher(dispatcherProvider.getDispatcher())
                .connectionPool(connectionPoolProvider.getConnectionPool());

        if (StringUtils.isNotBlank(okHttpConfigProperties.getProxyUrl())) {
            Proxy proxy = new Proxy(okHttpConfigProperties.getProxyType(),
                    new InetSocketAddress(okHttpConfigProperties.getProxyUrl(), okHttpConfigProperties.getProxyPort()));
            okHttpBuilder.proxy(proxy);
        }

        if (okHttpConfigProperties.isIgnoreSsl()) {
            System.setProperty("aihub.ssl.trust-all", "true");
            try {
                okHttpBuilder
                        .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                        .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }

        configuration.setOkHttpClient(okHttpBuilder.build());
    }

    private void initOpenAiConfig() {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        openAiConfig.setApiHost(openAiConfigProperties.getApiHost());
        openAiConfig.setApiKey(openAiConfigProperties.getApiKey());
        openAiConfig.setChatCompletionUrl(openAiConfigProperties.getChatCompletionUrl());
        openAiConfig.setEmbeddingUrl(openAiConfigProperties.getEmbeddingUrl());
        openAiConfig.setSpeechUrl(openAiConfigProperties.getSpeechUrl());
        openAiConfig.setTranscriptionUrl(openAiConfigProperties.getTranscriptionUrl());
        openAiConfig.setTranslationUrl(openAiConfigProperties.getTranslationUrl());
        openAiConfig.setRealtimeUrl(openAiConfigProperties.getRealtimeUrl());
        openAiConfig.setImageGenerationUrl(openAiConfigProperties.getImageGenerationUrl());
        openAiConfig.setResponsesUrl(openAiConfigProperties.getResponsesUrl());
        openAiConfig.setVideoUrl(openAiConfigProperties.getVideoUrl());
        configuration.setOpenAiConfig(openAiConfig);
    }

    private void initZhipuConfig() {
        ZhipuConfig zhipuConfig = new ZhipuConfig();
        zhipuConfig.setApiHost(zhipuConfigProperties.getApiHost());
        zhipuConfig.setApiKey(zhipuConfigProperties.getApiKey());
        zhipuConfig.setChatCompletionUrl(zhipuConfigProperties.getChatCompletionUrl());
        zhipuConfig.setEmbeddingUrl(zhipuConfigProperties.getEmbeddingUrl());
        configuration.setZhipuConfig(zhipuConfig);
    }

    private void initAnthropicConfig() {
        AnthropicConfig anthropicConfig = new AnthropicConfig();
        anthropicConfig.setApiHost(anthropicConfigProperties.getApiHost());
        anthropicConfig.setApiKey(anthropicConfigProperties.getApiKey());
        anthropicConfig.setChatCompletionUrl(anthropicConfigProperties.getChatCompletionUrl());
        anthropicConfig.setApiVersion(anthropicConfigProperties.getApiVersion());
        anthropicConfig.setStreamTimeoutMillis(anthropicConfigProperties.getStreamTimeoutMillis());
        configuration.setAnthropicConfig(anthropicConfig);
    }

    private void initDeepSeekConfig() {
        DeepSeekConfig deepSeekConfig = new DeepSeekConfig();
        deepSeekConfig.setApiHost(deepSeekConfigProperties.getApiHost());
        deepSeekConfig.setApiKey(deepSeekConfigProperties.getApiKey());
        deepSeekConfig.setChatCompletionUrl(deepSeekConfigProperties.getChatCompletionUrl());
        configuration.setDeepSeekConfig(deepSeekConfig);
    }

    private void initMoonshotConfig() {
        MoonshotConfig moonshotConfig = new MoonshotConfig();
        moonshotConfig.setApiHost(moonshotConfigProperties.getApiHost());
        moonshotConfig.setApiKey(moonshotConfigProperties.getApiKey());
        moonshotConfig.setChatCompletionUrl(moonshotConfigProperties.getChatCompletionUrl());
        configuration.setMoonshotConfig(moonshotConfig);
    }

    private void initHunyuanConfig() {
        HunyuanConfig hunyuanConfig = new HunyuanConfig();
        hunyuanConfig.setApiHost(hunyuanConfigProperties.getApiHost());
        hunyuanConfig.setApiKey(hunyuanConfigProperties.getApiKey());
        configuration.setHunyuanConfig(hunyuanConfig);
    }

    private void initLingyiConfig() {
        LingyiConfig lingyiConfig = new LingyiConfig();
        lingyiConfig.setApiHost(lingyiConfigProperties.getApiHost());
        lingyiConfig.setApiKey(lingyiConfigProperties.getApiKey());
        lingyiConfig.setChatCompletionUrl(lingyiConfigProperties.getChatCompletionUrl());
        configuration.setLingyiConfig(lingyiConfig);
    }

    private void initOllamaConfig() {
        OllamaConfig ollamaConfig = new OllamaConfig();
        ollamaConfig.setApiHost(ollamaConfigProperties.getApiHost());
        ollamaConfig.setApiKey(ollamaConfigProperties.getApiKey());
        ollamaConfig.setChatCompletionUrl(ollamaConfigProperties.getChatCompletionUrl());
        ollamaConfig.setEmbeddingUrl(ollamaConfigProperties.getEmbeddingUrl());
        ollamaConfig.setRerankUrl(ollamaConfigProperties.getRerankUrl());
        configuration.setOllamaConfig(ollamaConfig);
    }

    private void initMinimaxConfig() {
        MinimaxConfig minimaxConfig = new MinimaxConfig();
        minimaxConfig.setApiHost(minimaxConfigProperties.getApiHost());
        minimaxConfig.setApiKey(minimaxConfigProperties.getApiKey());
        minimaxConfig.setChatCompletionUrl(minimaxConfigProperties.getChatCompletionUrl());
        configuration.setMinimaxConfig(minimaxConfig);
    }

    private void initBaichuanConfig() {
        BaichuanConfig baichuanConfig = new BaichuanConfig();
        baichuanConfig.setApiHost(baichuanConfigProperties.getApiHost());
        baichuanConfig.setApiKey(baichuanConfigProperties.getApiKey());
        baichuanConfig.setChatCompletionUrl(baichuanConfigProperties.getChatCompletionUrl());
        configuration.setBaichuanConfig(baichuanConfig);
    }

    private void initDashScopeConfig() {
        DashScopeConfig dashScopeConfig = new DashScopeConfig();
        dashScopeConfig.setApiKey(dashScopeConfigProperties.getApiKey());
        dashScopeConfig.setApiHost(dashScopeConfigProperties.getApiHost());
        dashScopeConfig.setResponsesUrl(dashScopeConfigProperties.getResponsesUrl());
        configuration.setDashScopeConfig(dashScopeConfig);
    }

    private void initDoubaoConfig() {
        DoubaoConfig doubaoConfig = new DoubaoConfig();
        doubaoConfig.setApiHost(doubaoConfigProperties.getApiHost());
        doubaoConfig.setApiKey(doubaoConfigProperties.getApiKey());
        doubaoConfig.setChatCompletionUrl(doubaoConfigProperties.getChatCompletionUrl());
        doubaoConfig.setImageGenerationUrl(doubaoConfigProperties.getImageGenerationUrl());
        doubaoConfig.setResponsesUrl(doubaoConfigProperties.getResponsesUrl());
        doubaoConfig.setRerankApiHost(doubaoConfigProperties.getRerankApiHost());
        doubaoConfig.setRerankUrl(doubaoConfigProperties.getRerankUrl());
        configuration.setDoubaoConfig(doubaoConfig);
    }

    private void initJinaConfig() {
        JinaConfig jinaConfig = new JinaConfig();
        jinaConfig.setApiHost(jinaConfigProperties.getApiHost());
        jinaConfig.setApiKey(jinaConfigProperties.getApiKey());
        jinaConfig.setRerankUrl(jinaConfigProperties.getRerankUrl());
        configuration.setJinaConfig(jinaConfig);
    }

    private void initSunoConfig() {
        SunoConfig sunoConfig = new SunoConfig();
        sunoConfig.setApiHost(sunoConfigProperties.getApiHost());
        sunoConfig.setApiKey(sunoConfigProperties.getApiKey());
        sunoConfig.setMusicUrl(sunoConfigProperties.getMusicUrl());
        sunoConfig.setLyricsUrl(sunoConfigProperties.getLyricsUrl());
        sunoConfig.setFetchUrl(sunoConfigProperties.getFetchUrl());
        configuration.setSunoConfig(sunoConfig);
    }

    private void initGrokConfig() {
        GrokConfig grokConfig = new GrokConfig();
        grokConfig.setApiHost(grokConfigProperties.getApiHost());
        grokConfig.setApiKey(grokConfigProperties.getApiKey());
        grokConfig.setChatCompletionUrl(grokConfigProperties.getChatCompletionUrl());
        grokConfig.setEmbeddingUrl(grokConfigProperties.getEmbeddingUrl());
        grokConfig.setImageGenerationUrl(grokConfigProperties.getImageGenerationUrl());
        grokConfig.setVideoUrl(grokConfigProperties.getVideoUrl());
        grokConfig.setVideoCreateUrl(grokConfigProperties.getVideoCreateUrl());
        configuration.setGrokConfig(grokConfig);
    }

    private void initGeminiConfig() {
        GeminiConfig geminiConfig = new GeminiConfig();
        geminiConfig.setApiHost(geminiConfigProperties.getApiHost());
        geminiConfig.setApiKey(geminiConfigProperties.getApiKey());
        geminiConfig.setChatCompletionUrl(geminiConfigProperties.getChatCompletionUrl());
        geminiConfig.setEmbeddingUrl(geminiConfigProperties.getEmbeddingUrl());
        geminiConfig.setImageGenerationUrl(geminiConfigProperties.getImageGenerationUrl());
        configuration.setGeminiConfig(geminiConfig);
    }

    private void initOrcaRouterConfig() {
        OrcaRouterConfig orcaRouterConfig = new OrcaRouterConfig();
        orcaRouterConfig.setApiHost(orcaRouterConfigProperties.getApiHost());
        orcaRouterConfig.setApiKey(orcaRouterConfigProperties.getApiKey());
        orcaRouterConfig.setChatCompletionUrl(orcaRouterConfigProperties.getChatCompletionUrl());
        orcaRouterConfig.setEmbeddingUrl(orcaRouterConfigProperties.getEmbeddingUrl());
        orcaRouterConfig.setSpeechUrl(orcaRouterConfigProperties.getSpeechUrl());
        orcaRouterConfig.setTranscriptionUrl(orcaRouterConfigProperties.getTranscriptionUrl());
        orcaRouterConfig.setTranslationUrl(orcaRouterConfigProperties.getTranslationUrl());
        orcaRouterConfig.setImageGenerationUrl(orcaRouterConfigProperties.getImageGenerationUrl());
        orcaRouterConfig.setResponsesUrl(orcaRouterConfigProperties.getResponsesUrl());
        orcaRouterConfig.setIncludeCost(orcaRouterConfigProperties.isIncludeCost());
        configuration.setOrcaRouterConfig(orcaRouterConfig);
    }
}
