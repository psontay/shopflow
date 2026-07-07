package com.shopflow.payment.domain.exceptions;

import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {
    PAY_ERR_INSUFFICIENT_FUNDS("PAY-001", "Insufficient balance in account/wallet", HttpStatus.BAD_REQUEST),
    PAY_ERR_ACCOUNT_LOCKED("PAY-002", "Customer's bank account/wallet is locked or frozen", HttpStatus.BAD_REQUEST),
    PAY_ERR_LIMIT_EXCEEDED("PAY-003",
                           "Transaction exceeds daily or per-transaction limit of the account",
                           HttpStatus.BAD_REQUEST),

    PAY_ERR_QR_CODE_INVALID("PAY-004", "Incorrect or corrupted QR code format", HttpStatus.BAD_REQUEST),
    PAY_ERR_QR_CODE_EXPIRED("PAY-005", "The payment QR code has expired", HttpStatus.BAD_REQUEST),
    PAY_ERR_QR_CODE_USED("PAY-006",
                         "This static/dynamic QR code has already been scanned and paid",
                         HttpStatus.CONFLICT),

    PAY_ERR_ORDER_PAID("PAY-007", "This order has already been paid successfully", HttpStatus.CONFLICT),
    PAY_ERR_ORDER_EXPIRED("PAY-008", "The checkout session or order has expired", HttpStatus.GONE),
    PAY_ERR_AMOUNT_MISMATCH("PAY-009", "The transferred amount does not match the order total", HttpStatus.BAD_REQUEST),
    PAY_ERR_DUPLICATE_TRANSACTION("PAY-010",
                                  "Duplicate payment request detected (Idempotency failure)",
                                  HttpStatus.CONFLICT),

    PAY_ERR_GATEWAY_TIMEOUT("PAY-011", "Payment gateway connection timed out", HttpStatus.GATEWAY_TIMEOUT),
    PAY_ERR_GATEWAY_MAINTENANCE("PAY-012",
                                "The banking network or payment gateway is under maintenance",
                                HttpStatus.SERVICE_UNAVAILABLE),
    PAY_ERR_GATEWAY_REJECTED("PAY-013",
                             "The payment gateway or bank system rejected the transaction",
                             HttpStatus.BAD_GATEWAY),
    PAY_ERR_NETWORK_FAILURE("PAY-014",
                            "Internal network failure while connecting to payment service",
                            HttpStatus.INTERNAL_SERVER_ERROR),

    PAY_ERR_INVALID_SIGNATURE("PAY-015",
                              "Invalid digital signature or checksum from gateway webhook",
                              HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    PaymentErrorCode(String code, String message, HttpStatus httpStatus) {
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
