
package ru.site.dto.mapper;

import org.springframework.stereotype.Component;
import ru.site.domain.model.Ride;
import ru.site.dto.event.ClientRideCreatedEvent;

@Component
public class DtoRideMapper {

    public ClientRideCreatedEvent toEvent(Ride ride) {
        return ClientRideCreatedEvent.builder()
            .id(ride.getId())
            .driverId(ride.getDriverId())
            .clientId(ride.getClientId())
            .rideStatus(ride.getRideStatus())
            .startLatitude(ride.getStartLatitude())
            .startLongitude(ride.getStartLongitude())
            .endLatitude(ride.getEndLatitude())
            .endLongitude(ride.getEndLongitude())
            .createdAt(ride.getCreatedAt())
            .build();
    }

    public Ride fromEvent(ClientRideCreatedEvent event) {
        return new Ride(event.getId(), event.getDriverId(), event.getClientId(),
                        event.getRideStatus(), event.getStartLatitude(),
                        event.getStartLongitude(), event.getEndLatitude(),
                        event.getEndLongitude(),
                        event.getCreatedAt());
    }
}
