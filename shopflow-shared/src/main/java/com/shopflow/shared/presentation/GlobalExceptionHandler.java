package com.shopflow.shared.presentation;

import com.shopflow.shared.domain.DomainException;
import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode != null ? errorCode.getHttpStatus() : HttpStatus.BAD_REQUEST;
        String message = errorCode != null ? errorCode.getMessage() : ex.getMessage();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .status(status.value())
                                                .message(message)
                                                .data(null)
                                                .timestamp(Instant.now())
                                                .build();
        return ResponseEntity.status(status)
                             .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)

    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Void> response = ApiResponse.badRequest(null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(FieldError :: getDefaultMessage)
                                .collect(Collectors.joining(", "));

        ApiResponse<Void> response = ApiResponse.badRequest(null, "Data invalid: " + errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(Exception ex) {
        ex.printStackTrace();
        ApiResponse<Void> response = ApiResponse.internalServerError(null,
                                                                     "An unexpected system error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(response);
    }

}