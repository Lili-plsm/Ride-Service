package ru.site.datasource.mapper;

import org.springframework.stereotype.Component;
import ru.site.datasource.model.RideEntity;
import ru.site.domain.model.Ride;

@Component
public class RideMapper {

    public Ride toDomain(RideEntity ride) {

        return new Ride(ride.getId(), ride.getDriverId(), ride.getClientId(), ride.getRideStatus(),
                        ride.getStartLatitude(), ride.getStartLongitude(),
                        ride.getEndLatitude(), ride.getEndLongitude(),
                        null);
    }

    public RideEntity toEntity(Ride ride) {

        return new RideEntity(ride.getId(), ride.getDriverId(), ride.getClientId(),
                              ride.getRideStatus(), ride.getStartLatitude(),
                              ride.getStartLongitude(), ride.getEndLatitude(),
                              ride.getEndLongitude(),
                              ride.getCreatedAt());
    }
}
