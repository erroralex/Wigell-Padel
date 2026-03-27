package com.nilsson.padel.dto;

public record AddressRecord(
        Long id,
        String streetName,
        String city,
        String zipCode,
        String country
) {
}
