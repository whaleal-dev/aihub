package com.whaleal.aihub;

import com.whaleal.aihub.config.DoubaoConfig;
import com.whaleal.aihub.interceptor.ErrorInterceptor;
import com.whaleal.aihub.listener.ResponseSseListener;
import com.whaleal.aihub.platform.openai.response.entity.Response;
import com.whaleal.aihub.platform.openai.response.entity.ResponseRequest;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IResponsesService;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.network.OkHttpUtil;
import com.whaleal.aihub.test.LiveProviderTest;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;


@Category(LiveProviderTest.class)
public class DoubaoResponsesTest {

    private IResponsesService responsesService;

    @Before
    public void test_init() throws NoSuchAlgorithmException, KeyManagementException {
        String apiKey = System.getenv("ARK_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("DOUBAO_API_KEY");
        }
        Assume.assumeTrue(apiKey != null && !apiKey.isEmpty());

        DoubaoConfig doubaoConfig = new DoubaoConfig();
        doubaoConfig.setApiKey(apiKey);

        Configuration configuration = new Configuration();
        configuration.setDoubaoConfig(doubaoConfig);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new ErrorInterceptor())
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                .build();
        configuration.setOkHttpClient(okHttpClient);

        AiService aiService = new AiService(configuration);
        responsesService = aiService.getResponsesService(PlatformType.DOUBAO);
    }

    @Test
    public void test_responses_create() throws Exception {
        ResponseRequest request = ResponseRequest.builder()
                .model("doubao-seed-1-8-251228")
                .input("Summarize the Responses API in one sentence")
                .build();

        Response response = responsesService.create(request);
        System.out.println(response);
    }

    @Test
    public void test_responses_stream() throws Exception {
        ResponseRequest request = ResponseRequest.builder()
                .model("doubao-seed-1-8-251228")
                .input("Describe the Responses API in one sentence")
                .build();

        ResponseSseListener listener = new ResponseSseListener() {
            @Override
            protected void onEvent() {
                if (!getCurrText().isEmpty()) {
                    System.out.print(getCurrText());
                }
            }
        };

        responsesService.createStream(request, listener);
        System.out.println();
        System.out.println("stream finished");
        System.out.println(listener.getResponse());
    }
}


