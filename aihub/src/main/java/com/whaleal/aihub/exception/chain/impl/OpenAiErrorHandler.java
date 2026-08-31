package com.whaleal.aihub.exception.chain.impl;

import com.whaleal.aihub.convert.Jsons;
import com.whaleal.aihub.exception.CommonException;
import com.whaleal.aihub.exception.chain.AbstractErrorHandler;
import com.whaleal.aihub.exception.error.Error;
import com.whaleal.aihub.exception.error.OpenAiError;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @Author cly
 * @Description OpenAi错误处理
 *
 * [openai, zhipu, deepseek, lingyi, moonshot] 错误返回类似，这里共用一个处理类
 *
 * @Date 2024/9/18 21:01
 * @author 恒哥
 */
public class OpenAiErrorHandler extends AbstractErrorHandler {

    @Override
    public Error parseError(String errorInfo) {
        // 解析json字符串
        try{
            OpenAiError openAiError = Jsons.fromJson(errorInfo, OpenAiError.class);

            Error error = openAiError.getError();
            if(ObjectUtils.isEmpty(error)){
                // 交给下一个节点处理
                return nextHandler.parseError(errorInfo);
            }
            return error;
        }catch (Exception e){
            throw new CommonException(errorInfo);
        }

    }
}
