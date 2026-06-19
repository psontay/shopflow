package com.shopflow.shared.domain;

public record Address(
        String street,
        String city,
        String zipCode,
        String country
) {

    public Address {
        street = (street != null) ? street.trim() : null;
        city = (city != null) ? city.trim() : null;
        zipCode = (zipCode != null) ? zipCode.trim() : null;
        country = (country != null) ? country.trim() : null;
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }
    }

}
