package com.whaleal.aihub.service;

import com.whaleal.aihub.listener.RealtimeListener;
import okhttp3.WebSocket;

/**
 * @Author cly
 * @Description realtime服务接口
 * @Date 2024/10/12 16:30
 */
public interface IRealtimeService {
    WebSocket createRealtimeClient(String baseUrl, String apiKey, String model, RealtimeListener realtimeListener);
    WebSocket createRealtimeClient(String model, RealtimeListener realtimeListener);
}
