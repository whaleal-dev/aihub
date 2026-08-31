package com.whaleal.aihub.service;

import com.whaleal.aihub.config.AiPlatform;
import lombok.Data;

import java.util.List;

@Data
public class AiConfig {

    private List<AiPlatform> platforms;
}
