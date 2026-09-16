package com.sawmik.valkey.dto;

import java.time.LocalDateTime;

public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, Object data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data, LocalDateTime.now());
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null, LocalDateTime.now());
    }
}
