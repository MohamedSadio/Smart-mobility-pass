package com.smartmobility.usermobilitypassservice.mapper;

import com.smartmobility.usermobilitypassservice.dto.CreateUserRequest;
import com.smartmobility.usermobilitypassservice.dto.UserDTO;
import com.smartmobility.usermobilitypassservice.entity.User;
import com.smartmobility.usermobilitypassservice.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final MobilityPassMapper mobilityPassMapper;

    public UserMapper(MobilityPassMapper mobilityPassMapper) {
        this.mobilityPassMapper = mobilityPassMapper;
    }

    /**
     * Convertit une entité User en UserDTO
     */
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Ne pas inclure le mot de passe dans le DTO pour des raisons de sécurité

        // Convertir le MobilityPass si présent (éviter lazy loading exception)
        if (user.getMobilityPass() != null) {
            dto.setMobilityPass(mobilityPassMapper.toDto(user.getMobilityPass()));
        }

        return dto;
    }

    /**
     * Convertit un UserDTO en entité User
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : UserStatus.ACTIVE);

        return user;
    }

    /**
     * Convertit un CreateUserRequest en entité User
     */
    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword()); // TODO: Encoder avec BCrypt
        user.setStatus(UserStatus.ACTIVE);

        return user;
    }

    /**
     * Convertit un UserDTO en UserDTO simple (sans MobilityPass)
     * Utile pour éviter les boucles infinies ou lazy loading
     */
    public UserDTO toDtoWithoutPass(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    /**
     * Met à jour une entité User existante avec les données d'un DTO
     */
    public void updateEntityFromDto(UserDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
    }
}