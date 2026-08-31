package com.whaleal.aihub.function;

import com.whaleal.aihub.annotation.FunctionCall;
import com.whaleal.aihub.annotation.FunctionParameter;
import com.whaleal.aihub.annotation.FunctionRequest;

/**
 * 测试用的传统Function工具
 */
@FunctionCall(name = "weather", description = "获取指定城市的天气信息")
public class TestFunction {

    public WeatherResponse apply(WeatherRequest request) {
        WeatherResponse response = new WeatherResponse();
        response.city = request.city;
        response.temperature = 25; // 模拟温度
        response.condition = "晴天";
        response.humidity = 60;
        response.timestamp = System.currentTimeMillis();
        return response;
    }

    @FunctionRequest
    public static class WeatherRequest {
        @FunctionParameter(description = "城市名称", required = true)
        public String city;

        @FunctionParameter(description = "温度单位", required = false)
        public String unit = "celsius";
    }

    public static class WeatherResponse {
        public String city;
        public int temperature;
        public String condition;
        public int humidity;
        public long timestamp;
    }
}
