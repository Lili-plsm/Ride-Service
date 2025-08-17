package ru.site.datasource.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.site.datasource.mapper.RideMapper;
import ru.site.datasource.model.RideEntity;
import ru.site.datasource.repository.RideRepository;
import ru.site.domain.model.Ride;

@Service
public class RideRepositoryServiceImpl implements RideRepositoryService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;

    public RideRepositoryServiceImpl(RideRepository rideRepository,
                                     RideMapper rideMapper) {
        this.rideRepository = rideRepository;
        this.rideMapper = rideMapper;
    }

    public List<Ride> getAllRides() {
        List<RideEntity> ridesEntity = rideRepository.findAll();
        List<Ride> rides = new ArrayList<>();
        for (RideEntity rideEntity : ridesEntity) {
            rides.add(rideMapper.toDomain(rideEntity));
        }

        return rides;
    }

    public List<Ride> findByClientId(Long clientId) {

        List<RideEntity> rideEntities = rideRepository.findByClientId(clientId);

        List<Ride> rides = new ArrayList<>();
        for (
            RideEntity rideEntity : rideEntities)

        {
            rides.add(rideMapper.toDomain(rideEntity));
        }
        return rides;
    }

    public List<Ride> findByDriverId(Long driverId) {

        List<RideEntity> rideEntities = rideRepository.findByDriverId(driverId);

        List<Ride> rides = new ArrayList<>();
        for (
            RideEntity rideEntity : rideEntities)

        {
            rides.add(rideMapper.toDomain(rideEntity));
        }
        return rides;
    }

    @Override
    public RideEntity getRide(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        return rideRepository.findById(id).orElseThrow(
            ()
                -> new EntityNotFoundException("Поездка не найдена с ID: " +
                                               id));
    }

    @Override
    @Transactional
    public RideEntity saveRide(RideEntity ride) {
        if (ride.getId() != null) {
            Optional<RideEntity> existingRideOpt =
                rideRepository.findById(ride.getId());
            if (existingRideOpt.isPresent()) {
                RideEntity existingRide = existingRideOpt.get();
                existingRide.setDriverId(ride.getDriverId());
                existingRide.setClientId(ride.getClientId());
                existingRide.setStartLatitude(ride.getStartLatitude());
                existingRide.setStartLongitude(ride.getStartLongitude());
                existingRide.setEndLatitude(ride.getEndLatitude());
                existingRide.setEndLongitude(ride.getEndLongitude());
                existingRide.setRideStatus(ride.getRideStatus());
                return rideRepository.save(existingRide);
            }
        }
        return rideRepository.save(ride);
    }
}
