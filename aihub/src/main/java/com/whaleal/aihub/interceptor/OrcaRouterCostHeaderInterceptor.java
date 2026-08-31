package com.whaleal.aihub.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * OrcaRouter 按请求费用头：{@code X-OrcaRouter-Include-Cost: true}。
 *
 * @author 恒哥
 * @since 2026-08-31
 */
public class OrcaRouterCostHeaderInterceptor implements Interceptor {

    public static final String HEADER_NAME = "X-OrcaRouter-Include-Cost";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .header(HEADER_NAME, "true")
                .build();
        return chain.proceed(request);
    }
}
