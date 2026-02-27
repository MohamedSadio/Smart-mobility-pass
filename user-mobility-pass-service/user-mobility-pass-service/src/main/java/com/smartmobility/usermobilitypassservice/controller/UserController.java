package com.smartmobility.usermobilitypassservice.controller;

import com.smartmobility.usermobilitypassservice.dto.CreateUserRequest;
import com.smartmobility.usermobilitypassservice.dto.UpdateUserRequest;
import com.smartmobility.usermobilitypassservice.dto.UserDTO;
import com.smartmobility.usermobilitypassservice.entity.UserStatus;
import com.smartmobility.usermobilitypassservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Créer un nouvel utilisateur
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        log.info("REST - Création d'un utilisateur: {}", request.getEmail());
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Récupérer un utilisateur par ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.info("REST - Récupération de l'utilisateur: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupérer un utilisateur par email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        log.info("REST - Récupération de l'utilisateur par email: {}", email);
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupérer un utilisateur par numéro de téléphone
     * GET /api/users/phone/{phoneNumber}
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<UserDTO> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        log.info("REST - Récupération de l'utilisateur par téléphone: {}", phoneNumber);
        UserDTO user = userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupérer tous les utilisateurs
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("REST - Récupération de tous les utilisateurs");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer les utilisateurs par statut
     * GET /api/users/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserDTO>> getUsersByStatus(@PathVariable UserStatus status) {
        log.info("REST - Récupération des utilisateurs avec le statut: {}", status);
        List<UserDTO> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    /**
     * Rechercher des utilisateurs
     * GET /api/users/search?term=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String term) {
        log.info("REST - Recherche d'utilisateurs avec le terme: {}", term);
        List<UserDTO> users = userService.searchUsers(term);
        return ResponseEntity.ok(users);
    }

    /**
     * Mettre à jour un utilisateur
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        log.info("REST - Mise à jour de l'utilisateur: {}", id);
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Mettre à jour le statut d'un utilisateur
     * PATCH /api/users/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable UUID id,
            @RequestParam UserStatus status) {
        log.info("REST - Mise à jour du statut de l'utilisateur {} vers {}", id, status);
        UserDTO updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Supprimer un utilisateur
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("REST - Suppression de l'utilisateur: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}