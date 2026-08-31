package com.whaleal.aihub.platform.jina.rerank;

import com.whaleal.aihub.config.JinaConfig;
import com.whaleal.aihub.platform.standard.rerank.StandardRerankService;
import com.whaleal.aihub.service.Configuration;

public class JinaRerankService extends StandardRerankService {

    public JinaRerankService(Configuration configuration) {
        this(configuration, configuration == null ? null : configuration.getJinaConfig());
    }

    public JinaRerankService(Configuration configuration, JinaConfig jinaConfig) {
        super(configuration == null ? null : configuration.getOkHttpClient(),
                jinaConfig == null ? null : jinaConfig.getApiHost(),
                jinaConfig == null ? null : jinaConfig.getApiKey(),
                jinaConfig == null ? null : jinaConfig.getRerankUrl());
    }
}
