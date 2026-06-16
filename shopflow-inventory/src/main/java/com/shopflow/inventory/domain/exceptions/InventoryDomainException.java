package com.shopflow.inventory.domain.exceptions;

import com.shopflow.shared.domain.DomainException;
import com.shopflow.shared.domain.ErrorCode;

public class InventoryDomainException extends DomainException {

    private final ErrorCode errorCode;

    public InventoryDomainException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
