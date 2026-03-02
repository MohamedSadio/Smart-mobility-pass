package com.smartmobility.authservice.service;

import com.smartmobility.authservice.client.UserServiceClient;
import com.smartmobility.authservice.dto.AuthDto;
import com.smartmobility.authservice.dto.UserServiceDto;
import com.smartmobility.authservice.exception.AuthException;
import com.smartmobility.authservice.security.JwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtUtil           jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        log.info("[AUTH] Inscription — email={}", request.email());

        UserServiceDto.UserResponse user = userServiceClient.createUser(
                new UserServiceDto.CreateUserRequest(
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.password(),
                        "USER",
                        request.phoneNumber()
                )
        );

        log.info("[AUTH] Utilisateur créé — userId={}, role={}", user.id(), user.role());

        String token = jwtUtil.generateToken(user.email(), user.id(), user.role());

        return new AuthDto.AuthResponse(token, "Bearer", user.email(), user.role(), 86400000L);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        log.info("[AUTH] Connexion — email={}", request.email());

        UserServiceDto.UserResponse user;
        try {
            user = userServiceClient.getByEmail(request.email());
        } catch (FeignException.NotFound e) {
            throw new AuthException("Email ou mot de passe incorrect.");
        }

        // Vérification BCrypt : mot de passe saisi vs mot de passe hashé en base
        if (!passwordEncoder.matches(request.password(), user.password())) {
            log.warn("[AUTH] Mot de passe incorrect — email={}", request.email());
            throw new AuthException("Email ou mot de passe incorrect.");
        }

        log.info("[AUTH] Connexion réussie — userId={}, role={}", user.id(), user.role());

        String token = jwtUtil.generateToken(user.email(), user.id(), user.role());

        return new AuthDto.AuthResponse(token, "Bearer", user.email(), user.role(), 86400000L);
    }
}