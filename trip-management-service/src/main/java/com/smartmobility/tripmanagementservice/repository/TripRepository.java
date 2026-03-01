package com.smartmobility.tripmanagementservice.repository;

import com.smartmobility.tripmanagementservice.entity.Trip;
import com.smartmobility.tripmanagementservice.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findByPassNumberOrderByCreatedAtDesc(String passNumber);

    List<Trip> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Trip> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, TripStatus status);
}