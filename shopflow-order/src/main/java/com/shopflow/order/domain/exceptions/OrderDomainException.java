package com.shopflow.order.domain.exceptions;

import com.shopflow.shared.domain.DomainException;
import com.shopflow.shared.domain.ErrorCode;

public class OrderDomainException extends DomainException {

    private final ErrorCode errorCode;

    public OrderDomainException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
