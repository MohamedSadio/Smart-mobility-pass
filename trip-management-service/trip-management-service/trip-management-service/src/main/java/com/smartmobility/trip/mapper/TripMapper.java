package com.smartmobility.trip.mapper;

import com.smartmobility.trip.dto.TripDto;
import com.smartmobility.trip.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "baseFare", ignore = true)
    @Mapping(target = "finalFare", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Trip toEntity(TripDto.TripRequest request);

    @Mapping(target = "status", expression = "java(trip.getStatus().name())")
    TripDto.TripResponse toResponse(Trip trip);

    List<TripDto.TripResponse> toResponseList(List<Trip> trips);
}
