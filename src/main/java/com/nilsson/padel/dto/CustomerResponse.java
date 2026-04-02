package com.nilsson.padel.dto;

public record CustomerResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        AddressRecord address
) {}