package com.nilsson.padel.dto;

public record CustomerRegistrationRequest(
        String username,
        String role,
        String firstName,
        String lastName,
        String email,
        String password
) {
}
