package com.smartmobility.tripmanagementservice.mapper;

import com.smartmobility.tripmanagementservice.dto.TripDto;
import com.smartmobility.tripmanagementservice.entity.Trip;
import com.smartmobility.tripmanagementservice.event.TripEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TripMapper {

    public TripDto.TripResponse toDto(Trip trip) {
        return new TripDto.TripResponse(
                trip.getId(),
                trip.getPassNumber(),
                trip.getUserId(),
                trip.getTransportType().name(),
                trip.getStartStation(),
                trip.getEndStation(),
                trip.getDistanceKm(),
                trip.getBaseFare(),
                trip.getDiscount(),
                trip.getFinalFare(),
                trip.getStatus().name(),
                trip.getFailureReason(),
                trip.getCreatedAt()
        );
    }

    public TripEvent toEvent(Trip trip, String eventType, String passStatus) {
        return new TripEvent(
                trip.getId(),
                "TRIP",
                trip.getUserId(),
                trip.getPassNumber(),
                eventType,
                trip.getFinalFare(),
                null,
                passStatus,
                trip.getTransportType().name(),
                trip.getStartStation(),
                trip.getEndStation(),
                LocalDateTime.now()
        );
    }
}