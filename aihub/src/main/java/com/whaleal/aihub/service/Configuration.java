package com.whaleal.aihub.service;

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
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.util.concurrent.TimeUnit;

/**
 * 统一的配置管理
 *
 * @Author cly
 * @author 恒哥
 */
@Data
public class Configuration {

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.SECONDS)
            .build();

    public EventSource.Factory createRequestFactory() {
        return EventSources.createFactory(okHttpClient);
    }

    private OpenAiConfig openAiConfig;
    private AnthropicConfig anthropicConfig;
    private ZhipuConfig zhipuConfig;
    private DeepSeekConfig deepSeekConfig;
    private MoonshotConfig moonshotConfig;
    private HunyuanConfig hunyuanConfig;
    private LingyiConfig lingyiConfig;
    private OllamaConfig ollamaConfig;
    private MinimaxConfig minimaxConfig;
    private BaichuanConfig baichuanConfig;
    private DashScopeConfig dashScopeConfig;
    private DoubaoConfig doubaoConfig;
    private JinaConfig jinaConfig;
    private SunoConfig sunoConfig;
    private GrokConfig grokConfig;
    private GeminiConfig geminiConfig;
    private OrcaRouterConfig orcaRouterConfig;
}
