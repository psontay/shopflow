package com.shopflow.inventory.domain.exceptions;

import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public enum InventoryErrorCode implements ErrorCode {

    PRODUCT_STOCK_NOT_FOUND("INV-001", "Product stock record not found", HttpStatus.NOT_FOUND),
    WAREHOUSE_NOT_FOUND("INV-002", "Warehouse not found", HttpStatus.NOT_FOUND),

    INVALID_STOCK_QUANTITY("INV-003", "Stock quantity cannot be negative or zero", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("INV-004", "Insufficient stock for the requested product", HttpStatus.BAD_REQUEST),

    RESERVATION_NOT_FOUND("INV-005", "Stock reservation not found or expired", HttpStatus.NOT_FOUND),
    RESERVATION_ALREADY_COMPLETED("INV-006", "Stock reservation has already been completed", HttpStatus.BAD_REQUEST),
    RESERVATION_ALREADY_CANCELED("INV-007", "Stock reservation has already been canceled", HttpStatus.BAD_REQUEST),

    STOCK_LOCKED("INV-008", "Product stock is currently locked for maintenance", HttpStatus.CONFLICT),
    PRODUCT_NOT_FOUND("INV-009", "Product not found", HttpStatus.NOT_FOUND),
    INVALID_ARGUMENT("INV-010", "Arg cannot be null", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    InventoryErrorCode(String code, String message, HttpStatus httpStatus) {
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