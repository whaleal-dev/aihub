package com.whaleal.aihub.service;

import com.whaleal.aihub.listener.ResponseSseListener;
import com.whaleal.aihub.platform.openai.response.entity.Response;
import com.whaleal.aihub.platform.openai.response.entity.ResponseDeleteResponse;
import com.whaleal.aihub.platform.openai.response.entity.ResponseRequest;


public interface IResponsesService {

    Response create(String baseUrl, String apiKey, ResponseRequest request) throws Exception;

    Response create(ResponseRequest request) throws Exception;

    void createStream(String baseUrl, String apiKey, ResponseRequest request, ResponseSseListener listener) throws Exception;

    void createStream(ResponseRequest request, ResponseSseListener listener) throws Exception;

    Response retrieve(String baseUrl, String apiKey, String responseId) throws Exception;

    Response retrieve(String responseId) throws Exception;

    ResponseDeleteResponse delete(String baseUrl, String apiKey, String responseId) throws Exception;

    ResponseDeleteResponse delete(String responseId) throws Exception;
}

