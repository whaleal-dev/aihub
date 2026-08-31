package com.whaleal.aihub.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ModelType {
    REALTIME("realtime"),
    EMBEDDING("embedding"),
    CHAT("chat"),
    ;
    private final String type;
}
