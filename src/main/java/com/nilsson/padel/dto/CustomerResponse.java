package com.nilsson.padel.dto;

public record CustomerResponse(
        Long id,
        String username,
        String role,
        String firstName,
        String lastName,
        AddressRecord address
) {}