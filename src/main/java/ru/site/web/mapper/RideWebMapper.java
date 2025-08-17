package ru.site.web.mapper;

import org.springframework.stereotype.Component;
import ru.site.domain.model.Ride;
import ru.site.web.model.RideRequest;
import ru.site.web.model.RideResponse;

@Component
public class RideWebMapper {

    public Ride fromRequest(RideRequest request) {
        Ride ride = new Ride();

        ride.setRideStatus(request.getRideStatus());
        ride.setStartLatitude(request.getStartLatitude());
        ride.setStartLongitude(request.getStartLongitude());
        ride.setEndLatitude(request.getEndLatitude());
        ride.setEndLongitude(request.getEndLongitude());

        return ride;
    }

    public RideResponse toResponse(Ride ride, String message) {
        RideResponse response = new RideResponse();

        response.setRideId(ride.getId());
        response.setStatus(ride.getRideStatus());
        response.setCreatedAt(ride.getCreatedAt());
        response.setMessage(message);

        return response;
    }
}
