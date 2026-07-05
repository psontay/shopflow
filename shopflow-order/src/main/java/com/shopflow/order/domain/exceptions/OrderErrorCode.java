package com.shopflow.order.domain.exceptions;

import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND("ORD-001", "Order not found", HttpStatus.NOT_FOUND),

    ORDER_ALREADY_CANCELED("ORD-002", "Order has already canceled", HttpStatus.BAD_REQUEST),

    INVALID_ORDER_STATE("ORD-003", "Order's status invalid to do this", HttpStatus.BAD_REQUEST),

    INSUFFICIENT_STOCK("ORD-004", "Stock not enough", HttpStatus.BAD_REQUEST);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    OrderErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
