package com.shopflow.shared.presentation;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;
    private final Instant timestamp;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(200)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(201)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> noContent(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(204)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> notModified(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(304)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> badRequest(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(400)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> unauthorized(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(401)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> forbidden(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(403)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> notFound(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(404)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> conflict(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(409)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> unprocessableEntity(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(422)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> tooManyRequests(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(429)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> internalServerError(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(500)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

    public static <T> ApiResponse<T> serviceUnavailable(T data, String message) {
        return ApiResponse.<T>builder()
                          .status(503)
                          .message(message)
                          .data(data)
                          .timestamp(Instant.now())
                          .build();
    }

}
