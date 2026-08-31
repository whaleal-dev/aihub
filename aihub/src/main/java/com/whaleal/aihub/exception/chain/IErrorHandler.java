package com.whaleal.aihub.exception.chain;

import com.whaleal.aihub.exception.error.Error;

/**
 * @Author cly
 * @Description 错误处理接口
 * @Date 2024/9/18 20:55
 */
public interface IErrorHandler {
    void setNext(IErrorHandler handler);
    Error parseError(String errorInfo);
}
