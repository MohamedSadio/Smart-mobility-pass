package com.smartmobility.trip.mapper;

import com.smartmobility.trip.dto.TripDto;
import com.smartmobility.trip.entity.Trip;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-22T16:15:31+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public Trip toEntity(TripDto.TripRequest request) {
        if ( request == null ) {
            return null;
        }

        Trip.TripBuilder trip = Trip.builder();

        trip.distanceKm( request.getDistanceKm() );
        trip.transportType( request.getTransportType() );
        trip.userId( request.getUserId() );

        return trip.build();
    }

    @Override
    public TripDto.TripResponse toResponse(Trip trip) {
        if ( trip == null ) {
            return null;
        }

        TripDto.TripResponse.TripResponseBuilder tripResponse = TripDto.TripResponse.builder();

        tripResponse.baseFare( trip.getBaseFare() );
        tripResponse.createdAt( trip.getCreatedAt() );
        tripResponse.distanceKm( trip.getDistanceKm() );
        tripResponse.finalFare( trip.getFinalFare() );
        tripResponse.id( trip.getId() );
        tripResponse.transportType( trip.getTransportType() );
        tripResponse.userId( trip.getUserId() );

        tripResponse.status( trip.getStatus().name() );

        return tripResponse.build();
    }

    @Override
    public List<TripDto.TripResponse> toResponseList(List<Trip> trips) {
        if ( trips == null ) {
            return null;
        }

        List<TripDto.TripResponse> list = new ArrayList<TripDto.TripResponse>( trips.size() );
        for ( Trip trip : trips ) {
            list.add( toResponse( trip ) );
        }

        return list;
    }
}
