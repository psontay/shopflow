package com.shopflow.identity.domain.models;

import com.shopflow.identity.domain.exceptions.IdentityDomainException;
import com.shopflow.identity.domain.exceptions.IdentityErrorCode;

/**
 * Address (Value Object) trong DDD.
 * Bất biến (Immutable) và so sánh dựa trên giá trị của các thuộc tính, không phải ID.
 */
public record Address(
        String street,
        String city,
        String zipCode,
        String country
) {
    public Address {
        // Validation luôn được đặt ở constructor của Value Object
        if (street == null || street.isBlank()) {
            throw new IdentityDomainException(IdentityErrorCode.INVALID_ADDRESS);
        }
        if (city == null || city.isBlank()) {
            throw new IdentityDomainException(IdentityErrorCode.INVALID_ADDRESS);
        }
        if (country == null || country.isBlank()) {
            throw new IdentityDomainException(IdentityErrorCode.INVALID_ADDRESS);
        }
    }
    
    // Value Object không có các hàm Setter (như setStreet, setCity)
    // Nếu muốn đổi địa chỉ, phải tạo ra một instance Address hoàn toàn mới
}
