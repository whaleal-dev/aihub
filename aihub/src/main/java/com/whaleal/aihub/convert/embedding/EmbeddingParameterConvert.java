package com.whaleal.aihub.convert.embedding;


import com.whaleal.aihub.platform.openai.embedding.entity.Embedding;

/**
 * EmbeddingParameterConvert
 * @param <T>
 */
public interface EmbeddingParameterConvert<T> {
    T convertEmbeddingRequest(Embedding embeddingRequest);
}
