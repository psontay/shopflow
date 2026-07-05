package com.shopflow.shared.domain;

public class SystemDomainException extends DomainException {

    private final ErrorCode errorCode;

    public SystemDomainException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
