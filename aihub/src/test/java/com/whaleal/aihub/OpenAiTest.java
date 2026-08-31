package com.whaleal.aihub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaleal.aihub.annotation.FunctionCall;
import com.whaleal.aihub.annotation.FunctionParameter;
import com.whaleal.aihub.annotation.FunctionRequest;
import com.whaleal.aihub.platform.openai.tool.Tool;
import com.whaleal.aihub.tool.ToolUtil;
import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.interceptor.ContentTypeInterceptor;
import com.whaleal.aihub.interceptor.ErrorInterceptor;
import com.whaleal.aihub.listener.RealtimeListener;
import com.whaleal.aihub.listener.SseListener;
import com.whaleal.aihub.network.ConnectionPoolProvider;
import com.whaleal.aihub.network.DispatcherProvider;
import com.whaleal.aihub.platform.openai.audio.entity.*;
import com.whaleal.aihub.platform.openai.audio.enums.AudioEnum;
import com.whaleal.aihub.platform.openai.chat.entity.*;
import com.whaleal.aihub.platform.openai.embedding.entity.Embedding;
import com.whaleal.aihub.platform.openai.embedding.entity.EmbeddingObject;
import com.whaleal.aihub.platform.openai.embedding.entity.EmbeddingResponse;
import com.whaleal.aihub.service.*;
import com.whaleal.aihub.service.factory.AiService;
import com.whaleal.aihub.network.OkHttpUtil;
import com.whaleal.aihub.service.spi.ServiceLoaderUtil;
import com.whaleal.aihub.test.LiveProviderTest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;
import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author cly
 * @Description OpenAi测试类
 * @Date 2024/8/3 18:22
 */
@Slf4j
@Category(LiveProviderTest.class)
public class OpenAiTest {

    private IEmbeddingService embeddingService;

    private IChatService chatService;

    private IAudioService audioService;
    private IRealtimeService realtimeService;
    Reflections reflections = new Reflections();

    @Before
    public void test_init() throws NoSuchAlgorithmException, KeyManagementException {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        String apiHost = LiveProviderTestSupport.firstEnv("OPENAI_API_HOST");
        if (!LiveProviderTestSupport.isBlank(apiHost)) {
            openAiConfig.setApiHost(apiHost);
        }
        openAiConfig.setApiKey(LiveProviderTestSupport.requireEnv(
                "Skip because OpenAI API key is not configured",
                "OPENAI_API_KEY"));


        Configuration configuration = new Configuration();
        configuration.setOpenAiConfig(openAiConfig);


        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        DispatcherProvider dispatcherProvider = ServiceLoaderUtil.load(DispatcherProvider.class);
        ConnectionPoolProvider connectionPoolProvider = ServiceLoaderUtil.load(ConnectionPoolProvider.class);
        Dispatcher dispatcher = dispatcherProvider.getDispatcher();
        ConnectionPool connectionPool = connectionPoolProvider.getConnectionPool();



        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new ContentTypeInterceptor())
                //.addInterceptor(new ErrorInterceptor())
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                .build();
        configuration.setOkHttpClient(okHttpClient);

        AiService aiService = new AiService(configuration);

        embeddingService = aiService.getEmbeddingService(PlatformType.OPENAI);

        //chatService = aiService.getChatService(PlatformType.getPlatform("OPENAI"));
        chatService = aiService.getChatService(PlatformType.OPENAI);

        audioService = aiService.getAudioService(PlatformType.OPENAI);

        realtimeService = aiService.getRealtimeService(PlatformType.OPENAI);
    }


    @Test
    public void test_embed() throws Exception {
        Embedding build = Embedding.builder()
                .input("The food was delicious and the waiter...")
                .model("text-embedding-ada-002")
                .build();
        System.out.println(build);

        EmbeddingResponse embedding = embeddingService.embedding(null, null, build);

        System.out.println(embedding);


    }


    @Test
    public void test_chatCompletions_common() throws Exception {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.withUser("鲁迅为什么打周树人"))
                .build();

        System.out.println("请求参数");
        System.out.println(chatCompletion);

        ChatCompletionResponse chatCompletionResponse = chatService.chatCompletion(chatCompletion);

        System.out.println("请求成功");
        System.out.println(chatCompletionResponse);

    }

    @Test
    public void test_chatCompletions_history() throws Exception {
        List<ChatMessage> history = new ArrayList<>();

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.withUser("鲁迅为什么打周树人"))
                .build();

        System.out.println("请求参数");
        System.out.println(chatCompletion);

        // 向历史中添加刚刚问过的消息
        history.add(chatCompletion.getMessages().get(chatCompletion.getMessages().size()-1));

        ChatCompletionResponse chatCompletionResponse = chatService.chatCompletion(chatCompletion);

        System.out.println("请求成功");
        System.out.println(chatCompletionResponse.getChoices().get(0).getMessage());
        // 将返回的消息添加到历史中
        history.add(chatCompletionResponse.getChoices().get(0).getMessage());


        // 开始第二次问答
        history.add(ChatMessage.withUser("我刚刚问了什么问题"));
        ChatCompletion chatCompletionWithHistory = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .messages(history)
                .build();
        ChatCompletionResponse chatCompletionResponseWithHistory = chatService.chatCompletion(chatCompletionWithHistory);

        System.out.println("请求成功");
        System.out.println(chatCompletionResponseWithHistory);

    }

    @Test
    public void test_chatCompletions_multimodal() throws Exception {
        // 当传递base64图片时的格式
        // "image_url": {"url": f"data:image/jpeg;base64,{base64_image}"},

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.withUser("这几张图片，分别有什么动物, 并且是什么品种",
                        "https://tse2-mm.cn.bing.net/th/id/OIP-C.SVxZtXIcz3LbcE4ZeS6jEgHaE7?w=231&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                        "https://ts3.cn.mm.bing.net/th?id=OIP-C.BYyILFgs3ATnTEQ-B5ApFQHaFj&w=288&h=216&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2"))
                .build();

        System.out.println("请求参数");
        System.out.println(chatCompletion);
        System.out.println(new ObjectMapper().writeValueAsString(chatCompletion));

        ChatCompletionResponse chatCompletionResponse = chatService.chatCompletion(chatCompletion);

        System.out.println("请求成功");
        System.out.println(chatCompletionResponse);
    }
    @Test
    public void test_chatCompletions_multimodal_stream() throws Exception {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4o")
                .message(ChatMessage.withUser("这几张图片，分别有什么动物, 并且是什么品种",
                        "https://tse2-mm.cn.bing.net/th/id/OIP-C.SVxZtXIcz3LbcE4ZeS6jEgHaE7?w=231&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                        "https://ts3.cn.mm.bing.net/th?id=OIP-C.BYyILFgs3ATnTEQ-B5ApFQHaFj&w=288&h=216&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2"))
                .build();


        System.out.println("请求参数");
        System.out.println(chatCompletion);

        // 构造监听器
        SseListener sseListener = new SseListener() {
            @Override
            protected void send() {
                log.info(this.getCurrStr());
            }
        };

        chatService.chatCompletionStream(chatCompletion, sseListener);

        System.out.println("请求成功");
        System.out.println(sseListener.getOutput());
        System.out.println(sseListener.getUsage());

    }

    @Test
    public void test_chatCompletions_stream() throws Exception {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("deepseek-reasoner")
                .message(ChatMessage.withUser("请思考，先有鸡还是先有蛋"))
                .build();


        System.out.println("请求参数");
        System.out.println(chatCompletion);


        // 构造监听器
        SseListener sseListener = new SseListener() {
            @Override
            protected void send() {
                long aaa = System.currentTimeMillis();
                //System.out.println(aaa - currentTimeMillis);
                log.info(this.getCurrStr());
            }
        };

        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始请求");
        chatService.chatCompletionStream(chatCompletion, sseListener);

        log.info("请求结束");
        long aaa = System.currentTimeMillis();
        System.out.println(aaa - currentTimeMillis);


        System.out.println(sseListener.getOutput());
        System.out.println(sseListener.getReasoningOutput());
        System.out.println(sseListener.getUsage());

    }


    @Test
    public void test_chatCompletions_stream_cancel() throws Exception {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4.1-nano")
                .message(ChatMessage.withUser("你好，你是谁"))
                .build();


        System.out.println("请求参数");
        System.out.println(chatCompletion);


        // 构造监听器
        SseListener sseListener = new SseListener() {
            @Override
            protected void error(Throwable t, Response response) {
                log.error("出错了");
                log.error(t.getMessage());
                log.error(response.message());
            }

            @Override
            protected void send() {
                long aaa = System.currentTimeMillis();
                //System.out.println(aaa - currentTimeMillis);


                if("我".equals(this.getCurrStr())) {
                    this.getEventSource().cancel();
                    log.warn("取消");
                }
                log.info(this.getCurrData());
            }
        };

        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始请求");
        chatService.chatCompletionStream(chatCompletion, sseListener);

        log.info("请求结束");
        long aaa = System.currentTimeMillis();
        System.out.println(aaa - currentTimeMillis);


        System.out.println(sseListener.getOutput());
        System.out.println(sseListener.getReasoningOutput());
        System.out.println(sseListener.getUsage());

    }
    @Test
    public void test_chatCompletions_function() throws Exception {
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.withUser("获取当前的时间"))
                .functions("queryWeather", "queryTrainInfo")
                .build();

        System.out.println("请求参数");
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(chatCompletion));

        ChatCompletionResponse chatCompletionResponse = chatService.chatCompletion(chatCompletion);

        System.out.println("请求成功");
        System.out.println(chatCompletionResponse);

        System.out.println(objectMapper.writeValueAsString(chatCompletion));

    }

    @Test
    public void test_chatCompletions_stream_function() throws Exception {

        // 构造请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.withUser("查询洛阳明天的天气，并告诉我火车是否发车"))
                .functions("queryWeather", "queryTrainInfo")
                .build();


        // 构造监听器
        SseListener sseListener = new SseListener() {
            @Override
            protected void send() {
                System.out.println(this.getCurrStr());
            }
        };
        // 显示函数参数，默认不显示
        sseListener.setShowToolArgs(true);

        // 发送SSE请求
        chatService.chatCompletionStream(chatCompletion, sseListener);
        System.out.println("完整内容： ");
        System.out.println(sseListener.getOutput());
        System.out.println("内容花费： ");
        System.out.println(sseListener.getUsage());
    }


    @Test
    public void test_text_to_speech() throws IOException {
        TextToSpeech speechRequest = TextToSpeech.builder()
                .input("你好，有什么我可以帮助你的吗？")
                .voice(AudioEnum.Voice.ECHO.getValue())
                .build();
        InputStream inputStream = audioService.textToSpeech(speechRequest);
        File outputFile = File.createTempFile("aihub-openai-tts-", ".mp3");
        outputFile.deleteOnExit();
        FileUtils.copyToFile(inputStream, outputFile);

    }

    @Test
    public void test_transcription(){
        Transcription request = Transcription.builder()
                .file(LiveProviderTestSupport.requireReadableFile(
                        "OPENAI_TEST_AUDIO_FILE",
                        "Skip because OpenAI audio input file is not configured"))
                .model("whisper-1")
                .build();

        TranscriptionResponse transcription = audioService.transcription(request);
        System.out.println(transcription);

    }

    @Test
    public void test_translation(){
        Translation request = Translation.builder()
                .file(LiveProviderTestSupport.requireReadableFile(
                        "OPENAI_TEST_AUDIO_FILE",
                        "Skip because OpenAI audio input file is not configured"))
                .model("whisper-1")
                .build();

        TranslationResponse translation = audioService.translation(request);
        System.out.println(translation);

    }


    @Test
    public void test_create_websocket(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        WebSocket realtimeClient = realtimeService.createRealtimeClient("gpt-4o-realtime-preview", new RealtimeListener() {
            @Override
            protected void onOpen(WebSocket webSocket) {
                log.info("OpenAi Realtime 连接成功");

                log.info("准备发送消息");
                webSocket.send("{\"type\":\"response.create\",\"response\":{\"modalities\":[\"text\"],\"instructions\":\"Please assist the user.\"}}");


                webSocket.close(1000, "OpenAi realtime client 关闭");
                //countDownLatch.countDown();

            }

            @Override
            protected void onMessage(ByteString bytes) {
                log.info("收到消息：{}", bytes.toString());
            }

            @Override
            protected void onMessage(String text) {
                log.info("收到消息：{}", text);
            }

            @Override
            protected void onFailure() {
                System.out.println("连接失败");
            }
        });

        System.out.println(11111111);


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @FunctionCall(name = "test_push_files", description = "Test push multiple files to repository")
    public static class TestPushFilesFunction implements java.util.function.Function<TestPushFilesFunction.Request, String> {

        @lombok.Data
        @FunctionRequest
        public static class Request {
            @FunctionParameter(description = "List of files to push")
            private java.util.List<String> files;

            @FunctionParameter(description = "Commit message")
            private String message;
        }

        @Override
        public String apply(Request request) {
            return "Files pushed: " + request.files.size();
        }
    }

    @org.junit.Test
    public void testArraySchemaGeneration() {
        System.out.println("=== 测试数组Schema生成 ===");

        try {
            // 测试传统Function工具
            Tool.Function function = ToolUtil.getFunctionEntity("test_push_files");
            if (function != null) {
                System.out.println("传统Function工具生成成功:");
                System.out.println("名称: " + function.getName());
                System.out.println("描述: " + function.getDescription());

                java.util.Map<String, Tool.Function.Property> properties = function.getParameters().getProperties();
                Tool.Function.Property filesProperty = properties.get("files");
                if (filesProperty != null) {
                    System.out.println("files属性类型: " + filesProperty.getType());
                    if (filesProperty.getItems() != null) {
                        System.out.println("files.items类型: " + filesProperty.getItems().getType());
                        System.out.println("✅ 数组Schema包含items定义 - 修复成功!");
                    } else {
                        System.out.println("❌ 数组Schema缺少items定义 - 修复失败!");
                    }
                } else {
                    System.out.println("❌ 未找到files属性");
                }
            } else {
                System.out.println("❌ 传统Function工具生成失败");
            }

            System.out.println("=== 测试完成 ===");

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



