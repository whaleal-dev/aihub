package com.whaleal.aihub.platform.ollama.rerank;

import com.whaleal.aihub.config.OllamaConfig;
import com.whaleal.aihub.platform.standard.rerank.StandardRerankService;
import com.whaleal.aihub.service.Configuration;

public class OllamaRerankService extends StandardRerankService {

    public OllamaRerankService(Configuration configuration) {
        this(configuration, configuration == null ? null : configuration.getOllamaConfig());
    }

    public OllamaRerankService(Configuration configuration, OllamaConfig ollamaConfig) {
        super(configuration == null ? null : configuration.getOkHttpClient(),
                ollamaConfig == null ? null : ollamaConfig.getApiHost(),
                ollamaConfig == null ? null : ollamaConfig.getApiKey(),
                ollamaConfig == null ? null : ollamaConfig.getRerankUrl());
    }
}
