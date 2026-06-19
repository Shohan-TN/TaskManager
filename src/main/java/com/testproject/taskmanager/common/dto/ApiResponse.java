package com.testproject.taskmanager.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T> {
    private int status;
    private String message;
    private T data;
    private ErrorResponse error;
    private MetaData meta;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null, new  MetaData(LocalDateTime.now()));
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null, null, new MetaData(LocalDateTime.now()));
    }

    public static <T> ApiResponse<T> error(int status, String message, ErrorResponse error) {
        return new ApiResponse<>(status, message, null, error, new MetaData(LocalDateTime.now()));
    }
}
