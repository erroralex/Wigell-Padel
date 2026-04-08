package com.nilsson.padel.security;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ──────────────────────────────────────────────
 * <h2>KeycloakConfig</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Konfigurerar Keycloak Admin Client.
 * Använder en dedikerad admin-klient för att säkert kunna skapa
 * och hantera användare via Client Credentials Grant.</p>
 * ──────────────────────────────────────────────
 */
@Configuration
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client.client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client.client-secret}")
    private String adminClientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}