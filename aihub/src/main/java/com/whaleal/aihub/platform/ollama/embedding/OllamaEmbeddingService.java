package com.whaleal.aihub.platform.ollama.embedding;

import com.whaleal.aihub.config.OllamaConfig;
import com.whaleal.aihub.convert.Jsons;
import com.whaleal.aihub.constant.Constants;
import com.whaleal.aihub.convert.embedding.EmbeddingParameterConvert;
import com.whaleal.aihub.convert.embedding.EmbeddingResultConvert;
import com.whaleal.aihub.platform.ollama.embedding.entity.OllamaEmbedding;
import com.whaleal.aihub.platform.ollama.embedding.entity.OllamaEmbeddingResponse;
import com.whaleal.aihub.platform.openai.embedding.entity.Embedding;
import com.whaleal.aihub.platform.openai.embedding.entity.EmbeddingObject;
import com.whaleal.aihub.platform.openai.embedding.entity.EmbeddingResponse;
import com.whaleal.aihub.platform.openai.usage.Usage;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IEmbeddingService;
import com.whaleal.aihub.network.UrlUtils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author cly
 * @Description TODO
 * @Date 2025/2/28 15:52
 * @author 恒哥
 */
public class OllamaEmbeddingService implements IEmbeddingService, EmbeddingParameterConvert<OllamaEmbedding>, EmbeddingResultConvert<OllamaEmbeddingResponse> {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get(Constants.APPLICATION_JSON);

    private final OllamaConfig ollamaConfig;
    private final OkHttpClient okHttpClient;

    public OllamaEmbeddingService(Configuration configuration) {
        this.ollamaConfig = configuration.getOllamaConfig();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    public OllamaEmbeddingService(Configuration configuration, OllamaConfig ollamaConfig) {
        this.ollamaConfig = ollamaConfig;
        this.okHttpClient = configuration.getOkHttpClient();
    }

    @Override
    public EmbeddingResponse embedding(String baseUrl, String apiKey, Embedding embeddingReq) throws Exception {
        if(baseUrl == null || "".equals(baseUrl)) baseUrl = ollamaConfig.getApiHost();
        if(apiKey == null || "".equals(apiKey)) apiKey = ollamaConfig.getApiKey();
        String jsonString = Jsons.toJson(convertEmbeddingRequest(embeddingReq));

        Request.Builder builder = new Request.Builder()
                .url(UrlUtils.concatUrl(baseUrl, ollamaConfig.getEmbeddingUrl()))
                .post(RequestBody.create(jsonString, JSON_MEDIA_TYPE));
        if(StringUtils.isNotBlank(apiKey)) {
            builder.header("Authorization", "Bearer " + apiKey);
        }
        Request request = builder.build();

        Response execute = okHttpClient.newCall(request).execute();
        if (execute.isSuccessful() && execute.body() != null) {
            OllamaEmbeddingResponse ollamaEmbeddingResponse = Jsons.fromJson(execute.body().string(), OllamaEmbeddingResponse.class);
            return convertEmbeddingResponse(ollamaEmbeddingResponse);
        }
        return null;
    }

    @Override
    public EmbeddingResponse embedding(Embedding embeddingReq) throws Exception {
        return this.embedding(null, null, embeddingReq);
    }

    @Override
    public OllamaEmbedding convertEmbeddingRequest(Embedding embeddingRequest) {
        Object input = embeddingRequest.getInput();
        if (input instanceof List<?>) {
            return OllamaEmbedding.builder()
                    .model(embeddingRequest.getModel())
                    .input(castStringList(input))
                    .build();
        }
        return OllamaEmbedding.builder()
                .model(embeddingRequest.getModel())
                .input((String) input)
                .build();
    }

    @Override
    public EmbeddingResponse convertEmbeddingResponse(OllamaEmbeddingResponse ollamaEmbeddingResponse) {
        EmbeddingResponse.EmbeddingResponseBuilder builder = EmbeddingResponse.builder()
                .model(ollamaEmbeddingResponse.getModel())
                .object("list")
                .usage(new Usage(ollamaEmbeddingResponse.getPromptEvalCount(), 0, ollamaEmbeddingResponse.getPromptEvalCount()));
        List<EmbeddingObject> embeddingObjects = new ArrayList<>();
        List<List<Float>> embeddings = ollamaEmbeddingResponse.getEmbeddings();
        for (int i = 0; i < embeddings.size(); i++) {
            EmbeddingObject embeddingObject = new EmbeddingObject();
            embeddingObject.setIndex(i);
            embeddingObject.setEmbedding(embeddings.get(i));
            embeddingObject.setObject("embedding");
            embeddingObjects.add(embeddingObject);
        }
        builder.data(embeddingObjects);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private List<String> castStringList(Object input) {
        return (List<String>) input;
    }
}

