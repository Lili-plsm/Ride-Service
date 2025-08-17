package ru.site.domain.service;

import java.util.List;
import ru.site.domain.model.Ride;

public interface RideService {
  public Ride saveRide(Ride ride);

  public Ride getRide(Long id);

  public void cancelRide(Long rideId, Long clientId);

  public List<Ride> findRidesByClientId(Long clientId);

  public Ride getCurrentRideByDriverId(Long driverId);

  public Ride getCurrentRideByClientId(Long clientId);

  public Ride getRideIfClientAuthorized(Long rideId, Long clientId);
}
