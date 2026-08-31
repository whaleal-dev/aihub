package com.whaleal.aihub.service;

import com.whaleal.aihub.rerank.entity.RerankRequest;
import com.whaleal.aihub.rerank.entity.RerankResponse;

public interface IRerankService {

    RerankResponse rerank(String baseUrl, String apiKey, RerankRequest request) throws Exception;

    RerankResponse rerank(RerankRequest request) throws Exception;
}
