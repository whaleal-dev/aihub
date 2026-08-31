package com.whaleal.aihub;

import com.whaleal.aihub.config.DoubaoConfig;
import com.whaleal.aihub.interceptor.ErrorInterceptor;
import com.whaleal.aihub.platform.openai.image.entity.ImageGeneration;
import com.whaleal.aihub.platform.openai.image.entity.ImageGenerationResponse;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IImageService;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.network.OkHttpUtil;
import com.whaleal.aihub.test.LiveProviderTest;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;
import org.junit.experimental.categories.Category;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @Author cly
 * @Description 豆包图片生成测试
 * @Date 2026/1/31
 */
@Category(LiveProviderTest.class)
public class DoubaoImageTest {

    private IImageService imageService;

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
        imageService = aiService.getImageService(PlatformType.DOUBAO);
    }

    @Test
    public void test_image_generate() throws Exception {
        ImageGeneration request = ImageGeneration.builder()
                .model("doubao-seedream-4-5-251128")
                .prompt("一只戴着飞行员护目镜的小猫，卡通风格，明亮配色")
                .size("2K")
                .responseFormat("url")
                .build();

        ImageGenerationResponse response = imageService.generate(request);
        System.out.println(response);
    }

    @Test
    public void test_image_generate_stream() throws Exception {
        ImageGeneration request = ImageGeneration.builder()
                .model("doubao-seedream-4-5-251128")
                .prompt("一只戴着飞行员护目镜的小猫，卡通风格，明亮配色")
                .size("2K")
                .responseFormat("url")
                .stream(true)
                .build();

        imageService.generateStream(request, new com.whaleal.aihub.listener.ImageSseListener() {
            @Override
            protected void onEvent() {
                System.out.println(getCurrEvent());
            }
        });

        System.out.println("stream finished");
    }
}


