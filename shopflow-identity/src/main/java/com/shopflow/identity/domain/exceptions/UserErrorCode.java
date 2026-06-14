package com.shopflow.identity.domain.exceptions;

import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USR-001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USR-002", "User already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_IN_USE("USR-003", "Email already in use", HttpStatus.CONFLICT),
    INVALID_USER_STATE("USR-004", "User's status invalid to do this", HttpStatus.BAD_REQUEST);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(String code, String message, HttpStatus httpStatus) {
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
