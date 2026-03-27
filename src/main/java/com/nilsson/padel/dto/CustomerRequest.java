package com.nilsson.padel.dto;

public record CustomerRequest(
        String username,
        String role,
        String firstName,
        String lastName,
        Long addressId
) {
}
