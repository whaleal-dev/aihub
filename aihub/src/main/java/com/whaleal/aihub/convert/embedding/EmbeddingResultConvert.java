package com.whaleal.aihub.convert.embedding;

import com.whaleal.aihub.platform.openai.embedding.entity.EmbeddingResponse;


/**
 * @Author cly
 * @param <T>
 */
public interface EmbeddingResultConvert<T> {
    EmbeddingResponse convertEmbeddingResponse(T t);
}
