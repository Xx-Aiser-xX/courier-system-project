package org.example.couriers.service.impl;

import jakarta.ws.rs.core.Response;
import org.example.couriers.service.KeycloakAdminService;
import org.example.courierscontract.exception.NonUniqueDataException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class KeycloakAdminServiceImpl implements KeycloakAdminService {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    private static final String ADMIN_REALM = "master";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private Keycloak keycloak;

    private Keycloak getKeycloak() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(ADMIN_REALM)
                    .clientId("admin-cli")
                    .username(ADMIN_USERNAME)
                    .password(ADMIN_PASSWORD)
                    .build();
        }
        return keycloak;
    }

    @Override
    public UUID registerUserInKeycloak(String email, String password, String roleName) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email);
        user.setEmail(email);
        user.setEmailVerified(true);

        user.setRequiredActions(Collections.emptyList());
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        UsersResource usersResource = getKeycloak().realm(realm).users();
        Response response = usersResource.create(user);

        if (response.getStatus() != 201)
            throw new NonUniqueDataException("ошибка регистрации в Keycloak: " + response.getStatus() + " " + response.getStatusInfo());

        String userId = CreatedResponseUtil.getCreatedId(response);

        RealmResource realmResource = getKeycloak().realm(realm);
        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));

        return UUID.fromString(userId);
    }
}
