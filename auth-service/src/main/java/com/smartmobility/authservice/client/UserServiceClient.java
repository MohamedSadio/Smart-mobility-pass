package com.smartmobility.authservice.client;

import com.smartmobility.authservice.client.fallback.UserServiceClientFallback;
import com.smartmobility.authservice.dto.UserServiceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-mobility-pass-service",
        fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {

    /** Créer un utilisateur lors de l'inscription */
    @PostMapping("/api/users")
    UserServiceDto.UserResponse createUser(@RequestBody UserServiceDto.CreateUserRequest request);

    /** Récupérer un utilisateur par email pour le login */
    @GetMapping("/api/users/email/{email}")
    UserServiceDto.UserResponse getByEmail(@PathVariable String email);
}