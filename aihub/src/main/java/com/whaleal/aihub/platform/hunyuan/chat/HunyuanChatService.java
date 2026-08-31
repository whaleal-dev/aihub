package com.whaleal.aihub.platform.hunyuan.chat;

import com.whaleal.aihub.config.HunyuanConfig;
import com.whaleal.aihub.constant.Constants;
import com.whaleal.aihub.convert.Jsons;
import com.whaleal.aihub.convert.chat.ParameterConvert;
import com.whaleal.aihub.convert.chat.ResultConvert;
import com.whaleal.aihub.exception.CommonException;
import com.whaleal.aihub.exception.HttpErrorDecoder;
import com.whaleal.aihub.listener.SseListener;
import com.whaleal.aihub.listener.StreamExecutionSupport;
import com.whaleal.aihub.platform.hunyuan.HunyuanConstant;
import com.whaleal.aihub.platform.hunyuan.chat.entity.HunyuanChatCompletion;
import com.whaleal.aihub.platform.hunyuan.chat.entity.HunyuanChatCompletionResponse;
import com.whaleal.aihub.platform.openai.chat.entity.*;
import com.whaleal.aihub.platform.openai.chat.entity.*;
import com.whaleal.aihub.platform.openai.tool.Tool;
import com.whaleal.aihub.platform.openai.tool.ToolCall;
import com.whaleal.aihub.platform.openai.usage.Usage;
import com.whaleal.aihub.service.Configuration;
import com.whaleal.aihub.service.IChatService;
import com.whaleal.aihub.auth.BearerTokenUtils;
import com.whaleal.aihub.platform.hunyuan.support.HunyuanJsonUtil;
import com.whaleal.aihub.tool.ToolUtil;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author cly
 * @Description 腾讯混元 Chat 服务
 * @Date 2024/8/30 19:24
 * @author 恒哥
 */
public class HunyuanChatService implements IChatService, ParameterConvert<HunyuanChatCompletion>, ResultConvert<HunyuanChatCompletionResponse> {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get(Constants.APPLICATION_JSON);

    private final HunyuanConfig hunyuanConfig;
    private final OkHttpClient okHttpClient;
    private final EventSource.Factory factory;

    public HunyuanChatService(Configuration configuration) {
        this.hunyuanConfig = configuration.getHunyuanConfig();
        this.okHttpClient = configuration.getOkHttpClient();
        this.factory = configuration.createRequestFactory();
    }

    public HunyuanChatService(Configuration configuration, HunyuanConfig hunyuanConfig) {
        this.hunyuanConfig = hunyuanConfig;
        this.okHttpClient = configuration.getOkHttpClient();
        this.factory = configuration.createRequestFactory();
    }


    @Override
    public HunyuanChatCompletion convertChatCompletionObject(ChatCompletion chatCompletion) {
        HunyuanChatCompletion hunyuanChatCompletion = new HunyuanChatCompletion();
        hunyuanChatCompletion.setModel(chatCompletion.getModel());
        hunyuanChatCompletion.setMessages(chatCompletion.getMessages());
        hunyuanChatCompletion.setStream(chatCompletion.getStream());
        hunyuanChatCompletion.setTemperature(chatCompletion.getTemperature());
        hunyuanChatCompletion.setTopP(chatCompletion.getTopP());
        hunyuanChatCompletion.setTools(chatCompletion.getTools());
        hunyuanChatCompletion.setFunctions(chatCompletion.getFunctions());
        hunyuanChatCompletion.setToolChoice(chatCompletion.getToolChoice());
        hunyuanChatCompletion.setExtraBody(chatCompletion.getExtraBody());
        return hunyuanChatCompletion;
    }

    @Override
    public EventSourceListener convertEventSource(SseListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    eventSourceListener.onEvent(eventSource, id, type, data);
                    return;
                }

                HunyuanChatCompletionResponse hunyuanChatCompletionResponse;
                try {
                    hunyuanChatCompletionResponse = Jsons.fromJson(HunyuanJsonUtil.toSnakeCaseJson(data), HunyuanChatCompletionResponse.class);
                } catch (Exception e) {
                    throw new CommonException("解析混元Hunyuan Chat Completion Response失败");
                }


                ChatCompletionResponse response = convertChatCompletionResponse(hunyuanChatCompletionResponse);
                response.setObject("chat.completion.chunk");

                Choice choice = response.getChoices().get(0);
                if(eventSourceListener.getToolCall()!=null){
                    if(choice.getDelta().getToolCalls()!=null){
                        choice.getDelta().getToolCalls().get(0).setId(null);
                    }
                }

                if(StringUtils.isBlank(choice.getFinishReason())){
                    response.setUsage(null);
                }


                if("tool_calls".equals(choice.getFinishReason())){
                    //eventSourceListener.setToolCall(null);
                    //this.onClosed(eventSource);
                }

                eventSourceListener.onEvent(eventSource, id, type, Jsons.toJson(response));
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }
        };
    }

    @Override
    public ChatCompletionResponse convertChatCompletionResponse(HunyuanChatCompletionResponse hunyuanChatCompletionResponse) {
        ChatCompletionResponse chatCompletionResponse = new ChatCompletionResponse();
        chatCompletionResponse.setId(hunyuanChatCompletionResponse.getId());
        chatCompletionResponse.setObject(hunyuanChatCompletionResponse.getObject());
        chatCompletionResponse.setCreated(Long.valueOf(hunyuanChatCompletionResponse.getCreated()));
        chatCompletionResponse.setModel(hunyuanChatCompletionResponse.getModel());
        chatCompletionResponse.setChoices(hunyuanChatCompletionResponse.getChoices());
        chatCompletionResponse.setUsage(hunyuanChatCompletionResponse.getUsage());
        return chatCompletionResponse;
    }

    @Override
    public ChatCompletionResponse chatCompletion(String baseUrl, String apiKey, ChatCompletion chatCompletion) throws Exception {
        try {
        if(baseUrl == null || "".equals(baseUrl)) baseUrl = hunyuanConfig.getApiHost();
        if(apiKey == null || "".equals(apiKey)) apiKey = hunyuanConfig.getApiKey();
        boolean passThroughToolCalls = Boolean.TRUE.equals(chatCompletion.getPassThroughToolCalls());
        chatCompletion.setStream(false);


        if(chatCompletion.getFunctions()!=null && !chatCompletion.getFunctions().isEmpty()){
            //List<Tool> tools = ToolUtil.getAllFunctionTools(chatCompletion.getFunctions());
            List<Tool> tools = ToolUtil.getAllFunctionTools(chatCompletion.getFunctions());
            chatCompletion.setTools(tools);
            if(tools == null){
                chatCompletion.setParallelToolCalls(null);
            }
        }
        if (chatCompletion.getTools()!=null && !chatCompletion.getTools().isEmpty()){

        }else{
            chatCompletion.setParallelToolCalls(null);
        }


        // 转换 请求参数
        HunyuanChatCompletion hunyuanChatCompletion = this.convertChatCompletionObject(chatCompletion);

        // 如含有function，则添加tool
/*        if(hunyuanChatCompletion.getFunctions()!=null && !hunyuanChatCompletion.getFunctions().isEmpty()){
            List<Tool> tools = ToolUtil.getAllFunctionTools(hunyuanChatCompletion.getFunctions());
            hunyuanChatCompletion.setTools(tools);
        }*/

        // 总token消耗
        Usage allUsage = new Usage();

        String finishReason = "first";

        while("first".equals(finishReason) || "tool_calls".equals(finishReason)){

            finishReason = null;

            String requestString = HunyuanJsonUtil.rewriteRequestBody(Jsons.toJson(hunyuanChatCompletion), chatCompletion.getModel());
            String authorization = BearerTokenUtils.getAuthorization(apiKey,HunyuanConstant.ChatCompletions,requestString);

            Request request = new Request.Builder()
                    .header("Authorization", authorization)
                    .header("X-TC-Action", HunyuanConstant.ChatCompletions)
                    .header("X-TC-Version", HunyuanConstant.Version)
                    .header("X-TC-Timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                    .url(baseUrl)
                    .post(RequestBody.create(requestString, JSON_MEDIA_TYPE))
                    .build();

            Response execute = okHttpClient.newCall(request).execute();
            if (execute.isSuccessful() && execute.body() != null){
                String responseString = execute.body().string();
                HunyuanChatCompletionResponse hunyuanChatCompletionResponse = Jsons.fromJson(
                        HunyuanJsonUtil.extractChatPayload(responseString), HunyuanChatCompletionResponse.class);

                Choice choice = hunyuanChatCompletionResponse.getChoices().get(0);
                finishReason = choice.getFinishReason();

                Usage usage = hunyuanChatCompletionResponse.getUsage();
                allUsage.setCompletionTokens(allUsage.getCompletionTokens() + usage.getCompletionTokens());
                allUsage.setTotalTokens(allUsage.getTotalTokens() + usage.getTotalTokens());
                allUsage.setPromptTokens(allUsage.getPromptTokens() + usage.getPromptTokens());

                // 判断是否为函数调用返回
                if("tool_calls".equals(finishReason)){
                    if (passThroughToolCalls) {
                        hunyuanChatCompletionResponse.setUsage(allUsage);
                        hunyuanChatCompletionResponse.setObject("chat.completion");
                        hunyuanChatCompletionResponse.setModel(hunyuanChatCompletion.getModel());
                        return this.convertChatCompletionResponse(hunyuanChatCompletionResponse);
                    }
                    ChatMessage message = choice.getMessage();
                    List<ToolCall> toolCalls = message.getToolCalls();

                    List<ChatMessage> messages = new ArrayList<>(hunyuanChatCompletion.getMessages());
                    messages.add(message);

                    // 添加 tool 消息
                    for (ToolCall toolCall : toolCalls) {
                        String functionName = toolCall.getFunction().getName();
                        String arguments = toolCall.getFunction().getArguments();
                        String functionResponse = ToolUtil.invoke(functionName, arguments);

                        messages.add(ChatMessage.withTool(functionResponse, toolCall.getId()));
                    }
                    hunyuanChatCompletion.setMessages(messages);

                }else{// 其他情况直接返回

                    // 设置包含tool的总token数
                    hunyuanChatCompletionResponse.setUsage(allUsage);
                    hunyuanChatCompletionResponse.setObject("chat.completion");
                    hunyuanChatCompletionResponse.setModel(hunyuanChatCompletion.getModel());

                    // 恢复原始请求数据
                    chatCompletion.setMessages(hunyuanChatCompletion.getMessages());
                    chatCompletion.setTools(hunyuanChatCompletion.getTools());

                    return this.convertChatCompletionResponse(hunyuanChatCompletionResponse);

                }

            } else {
                throw HttpErrorDecoder.decode(execute);
            }

        }



        return null;
        } finally {
        }
    }

    @Override
    public ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) throws Exception {
        return this.chatCompletion(null, null, chatCompletion);
    }

    @Override
    public void chatCompletionStream(String baseUrl, String apiKey, ChatCompletion chatCompletion, SseListener eventSourceListener) throws Exception {
        try {
        if(baseUrl == null || "".equals(baseUrl)) baseUrl = hunyuanConfig.getApiHost();
        if(apiKey == null || "".equals(apiKey)) apiKey = hunyuanConfig.getApiKey();
        chatCompletion.setStream(true);
        boolean passThroughToolCalls = Boolean.TRUE.equals(chatCompletion.getPassThroughToolCalls());

        if(chatCompletion.getFunctions()!=null && !chatCompletion.getFunctions().isEmpty()){
            //List<Tool> tools = ToolUtil.getAllFunctionTools(chatCompletion.getFunctions());
            List<Tool> tools = ToolUtil.getAllFunctionTools(chatCompletion.getFunctions());
            chatCompletion.setTools(tools);
            if(tools == null){
                chatCompletion.setParallelToolCalls(null);
            }
        }
        if (chatCompletion.getTools()!=null && !chatCompletion.getTools().isEmpty()){

        }else{
            chatCompletion.setParallelToolCalls(null);
        }


        // 转换 请求参数
        HunyuanChatCompletion hunyuanChatCompletion = this.convertChatCompletionObject(chatCompletion);

/*        // 如含有function，则添加tool
        if(hunyuanChatCompletion.getFunctions()!=null && !hunyuanChatCompletion.getFunctions().isEmpty()){
            List<Tool> tools = ToolUtil.getAllFunctionTools(hunyuanChatCompletion.getFunctions());
            hunyuanChatCompletion.setTools(tools);
        }*/

        String finishReason = "first";

        while("first".equals(finishReason) || "tool_calls".equals(finishReason)){

            finishReason = null;

            String requestString = HunyuanJsonUtil.rewriteRequestBody(Jsons.toJson(hunyuanChatCompletion), chatCompletion.getModel());
            String authorization = BearerTokenUtils.getAuthorization(apiKey,HunyuanConstant.ChatCompletions,requestString);

            Request request = new Request.Builder()
                    .header("Authorization", authorization)
                    .header("X-TC-Action", HunyuanConstant.ChatCompletions)
                    .header("X-TC-Version", HunyuanConstant.Version)
                    .header("X-TC-Timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                    .header("Accept", Constants.SSE_CONTENT_TYPE)
                    .url(baseUrl)
                    .post(RequestBody.create(requestString, JSON_MEDIA_TYPE))
                    .build();

            StreamExecutionSupport.execute(
                    eventSourceListener,
                    chatCompletion.getStreamExecution(),
                    () -> factory.newEventSource(request, convertEventSource(eventSourceListener))
            );

            finishReason = eventSourceListener.getFinishReason();
            List<ToolCall> toolCalls = eventSourceListener.getToolCalls();

            // 需要调用函数
            if("tool_calls".equals(finishReason) && !toolCalls.isEmpty()){
                if (passThroughToolCalls) {
                    return;
                }
                // 创建tool响应消息
                ChatMessage responseMessage = ChatMessage.withAssistant(eventSourceListener.getToolCalls());
                responseMessage.setContent(Content.ofText(" "));

                List<ChatMessage> messages = new ArrayList<>(hunyuanChatCompletion.getMessages());
                messages.add(responseMessage);

                // 封装tool结果消息
                for (ToolCall toolCall : toolCalls) {
                    String functionName = toolCall.getFunction().getName();
                    String arguments = toolCall.getFunction().getArguments();
                    String functionResponse = ToolUtil.invoke(functionName, arguments);

                    messages.add(ChatMessage.withTool(functionResponse, toolCall.getId()));
                }
                eventSourceListener.setToolCalls(new ArrayList<>());
                eventSourceListener.setToolCall(null);
                hunyuanChatCompletion.setMessages(messages);
            }

        }

        // 补全原始请求
        chatCompletion.setMessages(hunyuanChatCompletion.getMessages());
        chatCompletion.setTools(hunyuanChatCompletion.getTools());
        } finally {
        }
    }

    @Override
    public void chatCompletionStream(ChatCompletion chatCompletion, SseListener eventSourceListener) throws Exception {
        this.chatCompletionStream(null, null, chatCompletion, eventSourceListener);
    }
}


