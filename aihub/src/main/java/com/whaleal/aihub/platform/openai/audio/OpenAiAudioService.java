package com.whaleal.aihub.platform.openai.audio;

import com.fasterxml.jackson.databind.JsonNode;
import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.convert.Jsons;
import com.whaleal.aihub.constant.Constants;
import com.whaleal.aihub.exception.AihubException;
import com.whaleal.aihub.exception.AiClientException;
import com.whaleal.aihub.exception.HttpErrorDecoder;
import com.whaleal.aihub.platform.openai.audio.entity.TextToSpeech;
import com.whaleal.aihub.platform.openai.audio.entity.Transcription;
import com.whaleal.aihub.platform.openai.audio.entity.TranscriptionResponse;
import com.whaleal.aihub.platform.openai.audio.entity.Translation;
import com.whaleal.aihub.platform.openai.audio.entity.TranslationResponse;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IAudioService;
import com.whaleal.aihub.network.UrlUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.FilterInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author cly
 * @Description OpenAi音频服务
 * @Date 2024/10/10 23:36
 * @author 恒哥
 */
public class OpenAiAudioService implements IAudioService {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get(Constants.APPLICATION_JSON);
    private static final MediaType OCTET_STREAM_MEDIA_TYPE = MediaType.get("application/octet-stream");

    private final OpenAiConfig openAiConfig;
    private final OkHttpClient okHttpClient;

    public OpenAiAudioService(Configuration configuration) {
        this.openAiConfig = configuration.getOpenAiConfig();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    public OpenAiAudioService(Configuration configuration, OpenAiConfig openAiConfig) {
        this.openAiConfig = openAiConfig;
        this.okHttpClient = configuration.getOkHttpClient();
    }


    @Override
    public InputStream textToSpeech(String baseUrl, String apiKey, TextToSpeech textToSpeech) {
        String requestString = Jsons.toJson(textToSpeech);
        Request request = buildAuthorizedRequest(
                baseUrl,
                apiKey,
                openAiConfig.getSpeechUrl(),
                RequestBody.create(requestString, JSON_MEDIA_TYPE)
        );

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw HttpErrorDecoder.decode(response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new AiClientException(response.code(), "OpenAI speech response had no body");
            }
            InputStream stream = new ResponseInputStream(response, responseBody.byteStream());
            response = null; // ownership transferred to the returned stream
            return stream;
        } catch (IOException e) {
            throw new AihubException("OpenAI speech request failed: " + e.getMessage(), e);
        } finally {
            closeQuietly(response);
        }
    }

    @Override
    public InputStream textToSpeech(TextToSpeech textToSpeech) {
        return this.textToSpeech(null, null, textToSpeech);
    }

    /**
     * response_format=url 形态：网关不回音频流而是回 JSON（含音频下载 URL），
     * 零样本克隆（prompt_audio_url + IndexTTS 系模型）多走此形态。
     * 宽容提取 url / audio_url / data.url / data.audio_url，提取失败抛出携带响应片段的异常。
     */
    @Override
    public String textToSpeechUrl(String baseUrl, String apiKey, TextToSpeech textToSpeech) {
        Request request = buildAuthorizedRequest(
                baseUrl,
                apiKey,
                openAiConfig.getSpeechUrl(),
                RequestBody.create(Jsons.toJson(textToSpeech), JSON_MEDIA_TYPE)
        );
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw HttpErrorDecoder.decode(response);
            }
            ResponseBody responseBody = response.body();
            String payload = responseBody == null ? "" : responseBody.string();
            JsonNode root = readJsonOrNull(payload);
            String url = firstNonBlank(
                    jsonText(root, "url"),
                    jsonText(root, "audio_url"),
                    jsonText(root == null ? null : root.get("data"), "url"),
                    jsonText(root == null ? null : root.get("data"), "audio_url")
            );
            if (url == null || url.isEmpty()) {
                throw new AiClientException(response.code(),
                        "speech url response missing audio url: " + truncate(payload));
            }
            return url;
        } catch (IOException e) {
            throw new AihubException("OpenAI speech request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String textToSpeechUrl(TextToSpeech textToSpeech) {
        return this.textToSpeechUrl(null, null, textToSpeech);
    }

    @Override
    public TranscriptionResponse transcription(String baseUrl, String apiKey, Transcription transcription) {
        MultipartBody.Builder builder = newAudioMultipartBuilder(
                transcription.getFile(),
                transcription.getModel(),
                transcription.getTemperature()
        );
        if(StringUtils.isNotBlank(transcription.getLanguage())){
            builder.addFormDataPart("language", transcription.getLanguage());
        }
        if(StringUtils.isNotBlank(transcription.getPrompt())){
            builder.addFormDataPart("prompt", transcription.getPrompt());
        }
        if(StringUtils.isNotBlank(transcription.getResponseFormat())){
            builder.addFormDataPart("response_format", transcription.getResponseFormat());
        }

        return executeJsonRequest(
                buildAuthorizedRequest(baseUrl, apiKey, openAiConfig.getTranscriptionUrl(), builder.build()),
                TranscriptionResponse.class
        );
    }

    @Override
    public TranscriptionResponse transcription(Transcription transcription) {
        return this.transcription(null, null, transcription);
    }

    @Override
    public TranslationResponse translation(String baseUrl, String apiKey, Translation translation) {
        MultipartBody.Builder builder = newAudioMultipartBuilder(
                translation.getFile(),
                translation.getModel(),
                translation.getTemperature()
        );
        if(StringUtils.isNotBlank(translation.getPrompt())){
            builder.addFormDataPart("prompt", translation.getPrompt());
        }
        if(StringUtils.isNotBlank(translation.getResponseFormat())){
            builder.addFormDataPart("response_format", translation.getResponseFormat());
        }

        return executeJsonRequest(
                buildAuthorizedRequest(baseUrl, apiKey, openAiConfig.getTranslationUrl(), builder.build()),
                TranslationResponse.class
        );
    }

    @Override
    public TranslationResponse translation(Translation translation) {
        return this.translation(null, null, translation);
    }

    private Request buildAuthorizedRequest(String baseUrl, String apiKey, String path, RequestBody requestBody) {
        return new Request.Builder()
                .header("Authorization", "Bearer " + resolveApiKey(apiKey))
                .url(UrlUtils.concatUrl(resolveBaseUrl(baseUrl), path))
                .post(requestBody)
                .build();
    }

    private MultipartBody.Builder newAudioMultipartBuilder(File file, String model, Object temperature) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, OCTET_STREAM_MEDIA_TYPE))
                .addFormDataPart("model", model)
                .addFormDataPart("temperature", String.valueOf(temperature));
    }

    private <T> T executeJsonRequest(Request request, Class<T> responseType) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Jsons.fromJson(response.body().string(), responseType);
            }
            throw HttpErrorDecoder.decode(response);
        } catch (IOException e) {
            throw new AihubException("OpenAI audio request failed: " + e.getMessage(), e);
        }
    }

    private String resolveBaseUrl(String baseUrl) {
        return (baseUrl == null || "".equals(baseUrl)) ? openAiConfig.getApiHost() : baseUrl;
    }

    private String resolveApiKey(String apiKey) {
        return (apiKey == null || "".equals(apiKey)) ? openAiConfig.getApiKey() : apiKey;
    }

    private static void closeQuietly(Response response) {
        if (response != null) {
            response.close();
        }
    }

    private static JsonNode readJsonOrNull(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return null;
        }
        try {
            return Jsons.readTree(payload);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String jsonText(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || !value.isValueNode()) {
            return null;
        }
        String text = value.asText();
        return text == null || text.isEmpty() ? null : text;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    private static String truncate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= 300 ? value : value.substring(0, 300);
    }

    /**
     * Keep the HTTP response open until the caller finishes consuming the stream.
     */
    private static final class ResponseInputStream extends FilterInputStream {
        private final Response response;

        private ResponseInputStream(Response response, InputStream delegate) {
            super(delegate);
            this.response = response;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                response.close();
            }
        }
    }
}

