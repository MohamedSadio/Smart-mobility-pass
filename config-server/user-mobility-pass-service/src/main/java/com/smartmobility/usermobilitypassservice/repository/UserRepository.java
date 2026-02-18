package com.smartmobility.usermobilitypassservice.repository;

import com.smartmobility.usermobilitypassservice.entity.User;
import com.smartmobility.usermobilitypassservice.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> findByStatus(UserStatus status);

    List<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

    Collection<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String searchTerm, String searchTerm1);
}