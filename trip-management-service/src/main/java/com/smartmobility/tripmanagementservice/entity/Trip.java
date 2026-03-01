package com.smartmobility.tripmanagementservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trips", indexes = {
        @Index(name = "idx_trip_pass_number", columnList = "pass_number"),
        @Index(name = "idx_trip_user_id",     columnList = "user_id"),
        @Index(name = "idx_trip_status",      columnList = "status"),
        @Index(name = "idx_trip_created_at",  columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pass_number", nullable = false, length = 60)
    private String passNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false, length = 10)
    private TransportType transportType;

    @Column(name = "start_station", nullable = false, length = 100)
    private String startStation;

    @Column(name = "end_station", nullable = false, length = 100)
    private String endStation;

    /** Tarif de base avant réduction (retourné par PricingService). */
    @Column(name = "base_fare", precision = 10, scale = 2)
    private BigDecimal baseFare;

    /** Montant de la réduction appliquée. */
    @Column(precision = 10, scale = 2)
    private BigDecimal discount;

    /** Montant final débité (retourné par PricingService). */
    @Column(name = "final_fare", precision = 10, scale = 2)
    private BigDecimal finalFare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TripStatus status;

    /** Message d'erreur en cas de FAILED. */
    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Distance du trajet en km (valeur par défaut selon le type de transport). */
    @Column(name = "distance_km")
    private Double distanceKm;
}