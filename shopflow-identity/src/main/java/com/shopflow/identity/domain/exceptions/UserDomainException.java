package com.shopflow.identity.domain.exceptions;

import com.shopflow.shared.domain.DomainException;
import com.shopflow.shared.domain.ErrorCode;

public class UserDomainException extends DomainException {

    private final ErrorCode errorCode;

    public UserDomainException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
