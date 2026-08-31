package com.whaleal.aihub.exception;

/**
 * Base runtime exception for aihub shared abstractions.
 *
 * @author 恒哥
 * @since 2026-08-31
 */
public class AihubException extends RuntimeException {

    public AihubException(String message) {
        super(message);
    }

    public AihubException(String message, Throwable cause) {
        super(message, cause);
    }
}
