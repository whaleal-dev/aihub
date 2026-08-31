package com.whaleal.aihub.exception;

/**
 * Thrown when the API returns 429 (Too Many Requests).
 */
public class AiRateLimitException extends AiHttpException {

    public AiRateLimitException(int statusCode, String message) {
        super(statusCode, message);
    }

    public AiRateLimitException(int statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }
}
