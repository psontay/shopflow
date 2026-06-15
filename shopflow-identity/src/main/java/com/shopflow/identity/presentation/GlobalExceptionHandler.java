package com.shopflow.identity.presentation;

import com.shopflow.identity.domain.exceptions.UserDomainException;
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

    @ExceptionHandler(UserDomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(UserDomainException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .status(errorCode.getHttpStatus()
                                                                 .value())
                                                .message(errorCode.getMessage())
                                                .data(null)
                                                .timestamp(
                                                        Instant.now())
                                                .build();
        return ResponseEntity.status(errorCode.getHttpStatus())
                             .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(Exception ex) {
        ex.printStackTrace();
        ApiResponse<Void> response = ApiResponse.internalServerError(null,
                                                                     "An unexpected system error occurred. Pls try again later");

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Void> response = ApiResponse.badRequest(null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .status(HttpStatus.FORBIDDEN.value())
                                                .message("You do not have permission to access thi resource")
                                                .data(null)
                                                .timestamp(Instant.now())
                                                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(response);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                .message("Authentication failed: " + ex.getMessage())
                                                .data(null)
                                                .timestamp(Instant.now())
                                                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(response);
    }

}
