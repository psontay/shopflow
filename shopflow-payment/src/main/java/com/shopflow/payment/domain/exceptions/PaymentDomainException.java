package com.shopflow.payment.domain.exceptions;

import com.shopflow.shared.domain.DomainException;
import com.shopflow.shared.domain.ErrorCode;

public class PaymentDomainException extends DomainException {
    private final ErrorCode errorCode;
    public PaymentDomainException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
