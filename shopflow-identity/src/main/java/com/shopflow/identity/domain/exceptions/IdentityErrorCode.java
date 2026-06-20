package com.shopflow.identity.domain.exceptions;

import com.shopflow.shared.domain.ErrorCode;
import org.springframework.http.HttpStatus;

public enum IdentityErrorCode implements ErrorCode {

    USER_NOT_FOUND("IDN-001", "User not found in the system", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("IDN-002", "Username or email is already registered", HttpStatus.CONFLICT),

    INVALID_CREDENTIALS("IDN-003", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("IDN-004",
                          "Refresh token was expired. Please make a new signin request",
                          HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("IDN-005", "Refresh token is invalid or does not exist", HttpStatus.UNAUTHORIZED),

    INVALID_ADDRESS("IDN-006", "Address fields cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT("IDN-007", "Argument cannot be null or empty", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED_ACCESS("IDN-008", "You do not have permission to perform this action", HttpStatus.FORBIDDEN),

    INVALID_USER_STATE("IDN-009", "User status invalid", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    IdentityErrorCode(String code, String message, HttpStatus httpStatus) {
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
