package com.shopflow.identity.domain.exceptions;

import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USR-001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USR-002", "User already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_IN_USE("USR-003", "Email already in use", HttpStatus.CONFLICT),
    CONFIRM_PASSWORD_NOT_MATCH("USR-004", "Confirm password not match", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH("USR-005", "Password not match", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("USR-006", "Invalid credentials", HttpStatus.NOT_FOUND),
    OLD_PASSWORD_NOT_MATCH("USR-007", "Old password not match", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD("USR-008", "New password cannot be the same as the old password", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATE("USR-010", "User's status invalid to do this", HttpStatus.BAD_REQUEST);
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
