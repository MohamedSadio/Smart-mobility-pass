package com.smartmobility.usermobilitypassservice.service;

import com.smartmobility.usermobilitypassservice.dto.CreateUserRequest;
import com.smartmobility.usermobilitypassservice.dto.UpdateUserRequest;
import com.smartmobility.usermobilitypassservice.dto.UserDTO;
import com.smartmobility.usermobilitypassservice.entity.User;
import com.smartmobility.usermobilitypassservice.entity.UserStatus;
import com.smartmobility.usermobilitypassservice.exception.DuplicateResourceException;
import com.smartmobility.usermobilitypassservice.exception.ResourceNotFoundException;
import com.smartmobility.usermobilitypassservice.exception.ValidationException;
import com.smartmobility.usermobilitypassservice.mapper.UserMapper;
import com.smartmobility.usermobilitypassservice.repository.UserRepository;
import com.smartmobility.usermobilitypassservice.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final MobilityPassService mobilityPassService;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        log.info("Création d'un nouvel utilisateur: {}", request.getEmail());

        // Validation des données
        validateUserRequest(request);

        // Vérification des doublons
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Un utilisateur avec cet email existe déjà");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Un utilisateur avec ce numéro de téléphone existe déjà");
        }

        // Conversion et sauvegarde
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        // Créer automatiquement un Mobility Pass
        mobilityPassService.createMobilityPass(savedUser.getId());

        log.info("Utilisateur créé avec succès: ID {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    public UserDTO getUserById(UUID id) {
        log.info("Recherche de l'utilisateur avec l'ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        return userMapper.toDto(user);
    }

    public UserDTO getUserByEmail(String email) {
        log.info("Recherche de l'utilisateur avec l'email: {}", email);

        ValidationUtils.validateEmail(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        return userMapper.toDto(user);
    }

    public UserDTO getUserByPhoneNumber(String phoneNumber) {
        log.info("Recherche de l'utilisateur avec le numéro: {}", phoneNumber);

        ValidationUtils.validatePhoneNumber(phoneNumber);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec ce numéro"));
        return userMapper.toDto(user);
    }

    public List<UserDTO> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByStatus(UserStatus status) {
        log.info("Récupération des utilisateurs avec le statut: {}", status);
        return userRepository.findByStatus(status).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String searchTerm) {
        log.info("Recherche d'utilisateurs avec le terme: {}", searchTerm);

        ValidationUtils.validateNotEmpty(searchTerm, "Terme de recherche");

        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        searchTerm, searchTerm)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(UUID id, UpdateUserRequest request) {
        log.info("Mise à jour de l'utilisateur: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        // Validation et mise à jour des champs
        if (request.getFirstName() != null) {
            ValidationUtils.validateName(request.getFirstName(), "Prénom");
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            ValidationUtils.validateName(request.getLastName(), "Nom");
            user.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            ValidationUtils.validateEmail(request.getEmail());

            // Vérifier que l'email n'est pas déjà utilisé par un autre utilisateur
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Cet email est déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            ValidationUtils.validatePhoneNumber(request.getPhoneNumber());

            // Vérifier que le numéro n'est pas déjà utilisé par un autre utilisateur
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new DuplicateResourceException("Ce numéro de téléphone est déjà utilisé");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User updatedUser = userRepository.save(user);

        log.info("Utilisateur mis à jour avec succès");
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDTO updateUserStatus(UUID id, UserStatus status) {
        log.info("Mise à jour du statut de l'utilisateur {} vers {}", id, status);

        if (status == null) {
            throw new ValidationException("Le statut est obligatoire");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        user.setStatus(status);
        User updatedUser = userRepository.save(user);

        log.info("Statut mis à jour avec succès");
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.info("Suppression de l'utilisateur: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès");
    }

    /**
     * Validation complète d'une requête de création d'utilisateur
     */
    private void validateUserRequest(CreateUserRequest request) {
        ValidationUtils.validateName(request.getFirstName(), "Prénom");
        ValidationUtils.validateName(request.getLastName(), "Nom");
        ValidationUtils.validateEmail(request.getEmail());
        ValidationUtils.validatePhoneNumber(request.getPhoneNumber());
        ValidationUtils.validatePassword(request.getPassword());
    }
}