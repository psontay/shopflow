package com.shopflow.order.presentation.api;

import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.shared.domain.ErrorCode;
import com.shopflow.shared.presentation.ApiResponse;
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

    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(OrderDomainException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .status(errorCode.getHttpStatus()
                                                                 .value())
                                                .message(errorCode.getMessage())
                                                .data(null)
                                                .timestamp(Instant.now())
                                                .build();

        return ResponseEntity.status(errorCode.getHttpStatus())
                             .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.internalServerError(
                null,
                "System unavailable!"
                                                                    );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(FieldError :: getDefaultMessage)
                                .collect(Collectors.joining(", "));

        ApiResponse<Void> response = ApiResponse.badRequest(
                null,
                "Data invalid: " + errorMessage
                                                           );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }

}



