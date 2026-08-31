package com.whaleal.aihub.docs;

import com.whaleal.aihub.config.OpenAiConfig;
import com.whaleal.aihub.platform.openai.chat.OpenAiChatService;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletion;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletionResponse;
import com.whaleal.aihub.platform.openai.chat.entity.ChatMessage;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IChatService;
import com.whaleal.aihub.service.PlatformType;
import com.whaleal.aihub.service.factory.AiService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Test;

public class FirstChatCopyableCodeTest {

    @Test
    public void plainJavaFirstChatSnippetShouldCreateChatServiceWithDefaultClient() {
        OpenAiConfig openAiConfig = new OpenAiConfig();
        openAiConfig.setApiKey("unit-test-key");

        Configuration configuration = new Configuration();
        configuration.setOpenAiConfig(openAiConfig);

        AiService aiService = new AiService(configuration);
        IChatService chatService = aiService.getChatService(PlatformType.OPENAI);

        Assert.assertNotNull(configuration.getOkHttpClient());
        Assert.assertTrue(chatService instanceof OpenAiChatService);
    }

    @Test
    public void plainJavaFirstChatSnippetShouldReadTextFromLocalProviderDouble() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{"
                        + "\"id\":\"chatcmpl_docs_first_chat\","
                        + "\"object\":\"chat.completion\","
                        + "\"created\":1710000000,"
                        + "\"model\":\"gpt-test\","
                        + "\"choices\":[{"
                        + "\"index\":0,"
                        + "\"message\":{"
                        + "\"role\":\"assistant\","
                        + "\"content\":\"AIHub makes the first Java chat path explicit.\""
                        + "},"
                        + "\"finish_reason\":\"stop\""
                        + "}],"
                        + "\"usage\":{"
                        + "\"prompt_tokens\":8,"
                        + "\"completion_tokens\":9,"
                        + "\"total_tokens\":17"
                        + "}"
                        + "}"));

        server.start();
        try {
            OpenAiConfig openAiConfig = new OpenAiConfig();
            openAiConfig.setApiKey("unit-test-key");
            openAiConfig.setApiHost(server.url("/").toString());

            Configuration configuration = new Configuration();
            configuration.setOpenAiConfig(openAiConfig);

            AiService aiService = new AiService(configuration);
            IChatService chatService = aiService.getChatService(PlatformType.OPENAI);

            ChatCompletion request = ChatCompletion.builder()
                    .model("gpt-test")
                    .message(ChatMessage.withUser("用一句话介绍 AIHub"))
                    .build();

            ChatCompletionResponse response = chatService.chatCompletion(request);
            String text = response.getChoices().get(0).getMessage().getContent().getText();

            Assert.assertEquals("AIHub makes the first Java chat path explicit.", text);
            Assert.assertTrue(response.getUsage().getTotalTokens() == 17L);

            RecordedRequest recordedRequest = server.takeRequest();
            Assert.assertEquals("/v1/chat/completions", recordedRequest.getPath());
            Assert.assertEquals("Bearer unit-test-key", recordedRequest.getHeader("Authorization"));
        } finally {
            server.shutdown();
        }
    }
}
