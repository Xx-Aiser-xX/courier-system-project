package org.example.couriers.service;

import java.util.UUID;

public interface KeycloakAdminService {
    UUID registerUserInKeycloak(String email, String password, String roleName);
}