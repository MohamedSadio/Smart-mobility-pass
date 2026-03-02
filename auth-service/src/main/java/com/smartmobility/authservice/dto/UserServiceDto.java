package com.smartmobility.authservice.dto;

import java.util.UUID;

/**
 * DTOs miroirs du user-mobility-pass-service.
 */
public class UserServiceDto {

    /**
     * Requête de création d'utilisateur envoyée au user-mobility-pass-service.
     */
    public record CreateUserRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String role,
            String phoneNumber
    ) {}

    /**
     * Réponse du user-mobility-pass-service après création ou récupération.
     */
    public record UserResponse(
            UUID   id,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String password,
            String role,
            String status
    ) {}
}