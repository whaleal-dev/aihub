package com.whaleal.aihub.service;

import com.whaleal.aihub.listener.SseListener;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletion;
import com.whaleal.aihub.platform.openai.chat.entity.ChatCompletionResponse;

/**
 * @Author cly
 * @Description TODO
 * @Date 2024/8/2 23:15
 * @author 恒哥
 */
public interface IChatService {

    ChatCompletionResponse chatCompletion(String baseUrl, String apiKey, ChatCompletion chatCompletion) throws Exception;
    ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) throws Exception;
    void chatCompletionStream(String baseUrl, String apiKey, ChatCompletion chatCompletion, SseListener eventSourceListener) throws Exception;
    void chatCompletionStream(ChatCompletion chatCompletion, SseListener eventSourceListener) throws Exception;

}
