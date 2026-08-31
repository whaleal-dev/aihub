package com.whaleal.aihub.platform.openai.realtime;

import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.listener.RealtimeListener;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IRealtimeService;
import com.whaleal.aihub.network.UrlUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * @Author cly
 * @Description OpenAiRealtimeService
 * @Date 2024/10/12 16:39
 */
public class OpenAiRealtimeService implements IRealtimeService {
    private final OpenAiConfig openAiConfig;
    private final OkHttpClient okHttpClient;

    public OpenAiRealtimeService(Configuration configuration) {
        this.openAiConfig = configuration.getOpenAiConfig();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    public OpenAiRealtimeService(Configuration configuration, OpenAiConfig openAiConfig) {
        this.openAiConfig = openAiConfig;
        this.okHttpClient = configuration.getOkHttpClient();
    }


    @Override
    public WebSocket createRealtimeClient(String baseUrl, String apiKey, String model, RealtimeListener realtimeListener) {
        if(baseUrl == null || "".equals(baseUrl)) baseUrl = openAiConfig.getApiHost(); // url为HTTPS不影响
        if(apiKey == null || "".equals(apiKey)) apiKey = openAiConfig.getApiKey();

        String url = UrlUtils.concatUrl(baseUrl, openAiConfig.getRealtimeUrl(), "?model=" + model);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("OpenAI-Beta", "realtime=v1")
                .build();
        return okHttpClient.newWebSocket(request, realtimeListener);

    }

    @Override
    public WebSocket createRealtimeClient(String model, RealtimeListener realtimeListener) {
        return this.createRealtimeClient(null, null, model, realtimeListener);
    }
}

