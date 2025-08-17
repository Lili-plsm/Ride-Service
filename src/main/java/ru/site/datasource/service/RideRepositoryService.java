package ru.site.datasource.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.RideEntity;
import ru.site.domain.model.Ride;

@Service
public interface RideRepositoryService {
    RideEntity saveRide(RideEntity ride);
    List<Ride> findByClientId(Long clientId);
    List<Ride> findByDriverId(Long driverId);
    RideEntity getRide(Long id);
    List<Ride> getAllRides();
}
