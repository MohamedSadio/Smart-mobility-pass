package com.smartmobility.authservice.client.fallback;

import com.smartmobility.authservice.client.UserServiceClient;
import com.smartmobility.authservice.dto.UserServiceDto;
import com.smartmobility.authservice.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserServiceDto.UserResponse createUser(UserServiceDto.CreateUserRequest request) {
        throw new ServiceUnavailableException(
                "Le service utilisateur est indisponible. Inscription impossible.");
    }

    @Override
    public UserServiceDto.UserResponse getByEmail(String email) {
        throw new ServiceUnavailableException(
                "Le service utilisateur est indisponible. Connexion impossible.");
    }
}