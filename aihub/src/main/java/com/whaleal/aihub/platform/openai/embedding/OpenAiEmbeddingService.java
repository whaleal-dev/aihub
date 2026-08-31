package com.whaleal.aihub.platform.openai.embedding;

import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.convert.Jsons;
import com.whaleal.aihub.constant.Constants;
import com.whaleal.aihub.platform.openai.embedding.entity.Embedding;
import com.whaleal.aihub.platform.openai.embedding.entity.EmbeddingResponse;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IEmbeddingService;
import com.whaleal.aihub.network.UrlUtils;
import okhttp3.*;

/**
 * @Author cly
 * @Description TODO
 * @Date 2024/8/7 17:40
 * @author 恒哥
 */
public class OpenAiEmbeddingService implements IEmbeddingService {

    private final OpenAiConfig openAiConfig;
    private final OkHttpClient okHttpClient;

    public OpenAiEmbeddingService(Configuration configuration) {
        this.openAiConfig = configuration.getOpenAiConfig();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    public OpenAiEmbeddingService(Configuration configuration, OpenAiConfig openAiConfig) {
        this.openAiConfig = openAiConfig;
        this.okHttpClient = configuration.getOkHttpClient();
    }


    @Override
    public EmbeddingResponse embedding(String baseUrl, String apiKey, Embedding embeddingReq)  throws Exception  {
        if(baseUrl == null || "".equals(baseUrl)) baseUrl = openAiConfig.getApiHost();
        if(apiKey == null || "".equals(apiKey)) apiKey = openAiConfig.getApiKey();
        String jsonString = Jsons.toJson(embeddingReq);

        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + apiKey)
                .url(UrlUtils.concatUrl(baseUrl, openAiConfig.getEmbeddingUrl()))
                .post(RequestBody.create(jsonString, MediaType.get(Constants.APPLICATION_JSON)))
                .build();
        Response execute = okHttpClient.newCall(request).execute();
        if (execute.isSuccessful() && execute.body() != null) {
            return Jsons.fromJson(execute.body().string(), EmbeddingResponse.class);
        }
        return null;
    }

    @Override
    public EmbeddingResponse embedding(Embedding embeddingReq) throws Exception {
        return embedding(null, null, embeddingReq);
    }
}

