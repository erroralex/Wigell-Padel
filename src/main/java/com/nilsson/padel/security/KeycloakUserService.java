package com.nilsson.padel.security;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * ──────────────────────────────────────────────
 * <h2>KeycloakUserService</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Isolerar all kommunikation med Keycloak. Hanterar
 * skapande av användare, tilldelning av roller och borttagning (rollback).</p>
 * ──────────────────────────────────────────────
 */
@Service
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final String realm;

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    public KeycloakUserService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String createUserInKeycloak(String email, String username, String password, String firstName, String lastName) {
        logger.info("Försöker skapa användare {} i Keycloak...", username);

        UserRepresentation user = new UserRepresentation();
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() != 201) {
            logger.error("Kunde inte skapa användare i Keycloak. HTTP Status: {}", response.getStatus());
            throw new RuntimeException("Kunde inte skapa användare i Keycloak. Status: " + response.getStatus());
        }

        String keycloakId = CreatedResponseUtil.getCreatedId(response);
        logger.info("Användare skapad i Keycloak med ID: {}", keycloakId);

        assignUserRole(keycloakId);

        return keycloakId;
    }

    public void deleteUserInKeycloak(String keycloakId) {
        logger.warn("Raderar användare med ID {} från Keycloak (Kompenserande transaktion)", keycloakId);

        try {
            keycloak.realm(realm).users().get(keycloakId).remove();
        } catch (Exception e) {
            logger.error("Kunde inte radera användare i Keycloak vid rollback: {}", keycloakId, e);
        }
    }

    private void assignUserRole(String userId) {
        RoleRepresentation userRole = keycloak.realm(realm).roles().get("USER").toRepresentation();

        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(userRole));
        logger.info("Rollen 'USER' tilldelad till Keycloak-användare: {}", userId);
    }
}