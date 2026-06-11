package com.shopflow.order.presentation.api;

import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<?> handleDomainException(OrderDomainException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                             .body(Map.of("code",
                                          errorCode.getCode(),
                                          "message",
                                          errorCode.getMessage(),
                                          "type",
                                          "BUSINESS_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleSystemException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Map.of("error", "Error, pls try again", "type", "SYSTEM_ERROR"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
          .getFieldErrors()
          .forEach(
                  error -> errors.put(error.getField(), error.getDefaultMessage())
                  );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("errors", errors, "type", "VALIDATION_ERROR"));
    }

}


