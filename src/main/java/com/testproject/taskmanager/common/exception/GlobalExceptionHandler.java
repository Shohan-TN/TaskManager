package com.testproject.taskmanager.common.exception;


import com.testproject.taskmanager.auth.exception.InvalidCredentialsException;
import com.testproject.taskmanager.auth.exception.UserAccessDeniedException;
import com.testproject.taskmanager.auth.exception.UserAlreadyExistsException;
import com.testproject.taskmanager.auth.exception.UserNotFoundException;
import com.testproject.taskmanager.common.dto.ApiResponse;
import com.testproject.taskmanager.common.dto.ErrorResponse;
import com.testproject.taskmanager.task.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed",
                new ErrorResponse(400, "BAD_REQUEST", details));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleTaskNotFound(TaskNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Task not found",
                new ErrorResponse(404, "NOT_FOUND", List.of(ex.getMessage())));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(UserNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "User not found",
                new ErrorResponse(404, "NOT_FOUND", List.of(ex.getMessage())));
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAccessDenied(UserAccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Access denied",
                new ErrorResponse(403, "FORBIDDEN", List.of(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                new ErrorResponse(500, "INTERNAL_SERVER_ERROR", List.of("Something went wrong")));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return build( HttpStatus.CONFLICT, "User already exists",
             new ErrorResponse(409, "CONFLICT", List.of(ex.getMessage()))
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized",
                new ErrorResponse(401, "UNAUTHORISED", List.of("User is not authorised"))
        );
    }

    private ResponseEntity<ApiResponse<?>> build(HttpStatus status, String message, ErrorResponse error) {
        return ResponseEntity.status(status).body( ApiResponse.error(status.value(),message,error));
    }

}
