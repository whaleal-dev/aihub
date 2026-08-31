package com.whaleal.aihub.exception.chain.impl;

import com.whaleal.aihub.exception.chain.AbstractErrorHandler;
import com.whaleal.aihub.exception.error.Error;

/**
 * @Author cly
 * @Description 未知的错误处理，用于兜底处理
 * @Date 2024/9/18 21:08
 */
public class UnknownErrorHandler extends AbstractErrorHandler {
    @Override
    public Error parseError(String errorInfo) {
        Error error = new Error();

        error.setParam(null);
        error.setType("Unknown Type");
        error.setCode("Unknown Code");
        error.setMessage(errorInfo);

        return error;
    }
}
