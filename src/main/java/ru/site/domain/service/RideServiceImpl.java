package ru.site.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.site.datasource.enums.DriverStatus;
import ru.site.datasource.enums.RideStatus;
import ru.site.datasource.mapper.RideMapper;
import ru.site.datasource.model.RideEntity;
import ru.site.datasource.service.RideRepositoryService;
import ru.site.domain.model.Ride;
import ru.site.dto.event.*;
import ru.site.dto.mapper.DtoRideMapper;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepositoryService rideRepositoryService;
    private final RideMapper rideMapper;
    private final KafkaTemplate<String, String> kafkaStringTemplate;
    private final ObjectMapper mapper;
    private final DtoRideMapper dtoRideMapper;
    private final ServicesUtil servicesUtil;

    @Value("${pricing.price-per-km}")
    private double pricePerKm;

    @Autowired
    public RideServiceImpl(RideRepositoryService rideRepositoryService,
                           RideMapper rideMapper,
                           KafkaTemplate<String, String> kafkaStringTemplate,
                           ObjectMapper mapper,
                           DtoRideMapper dtoRideMapper,
                           ServicesUtil servicesUtil) {
        this.rideRepositoryService = rideRepositoryService;
        this.rideMapper = rideMapper;
        this.kafkaStringTemplate = kafkaStringTemplate;
        this.mapper = mapper;
        this.dtoRideMapper = dtoRideMapper;
        this.servicesUtil = servicesUtil;
    }

    public Ride saveRide(Ride ride) {
        RideEntity rideEntity = rideRepositoryService.saveRide(rideMapper.toEntity(ride));
        return rideMapper.toDomain(rideEntity);
    }

    public Ride getRide(Long id) {
        RideEntity rideEntity = rideRepositoryService.getRide(id);
        return rideMapper.toDomain(rideEntity);
    }

    public Ride getRideIfClientAuthorized(Long rideId, Long clientId) {
        Ride ride = getRide(rideId);
        if (ride == null) {
            throw new NoSuchElementException("Поездка не найдена");
        }
        if (!ride.getClientId().equals(clientId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }
        return ride;
    }
    public List<Ride> findRidesByClientId(Long clientId) {
        return rideRepositoryService.findByClientId(clientId);
    }

    public Ride getCurrentRideByClientId(Long clientId) {
        List<Ride> rides = rideRepositoryService.findByClientId(clientId);
        List<Ride> filteredRides = rides.stream()
                                       .filter(ride -> RideStatus.IN_PROGRESS.equals(ride.getRideStatus()) || RideStatus.ASSIGNED.equals(ride.getRideStatus()) || RideStatus.REQUESTED.equals(ride.getRideStatus()))
                                       .collect(Collectors.toList());

        return filteredRides.get(0);
    }

    public Ride getCurrentRideByDriverId(Long driverId) {
        List<Ride> rides = rideRepositoryService.findByDriverId(driverId);
        List<Ride> filteredRides = rides.stream()
                                       .filter(ride -> RideStatus.IN_PROGRESS.equals(ride.getRideStatus()) || RideStatus.ASSIGNED.equals(ride.getRideStatus()) || RideStatus.REQUESTED.equals(ride.getRideStatus()))
                                       .collect(Collectors.toList());

        return filteredRides.get(0);
    }

    public void cancelRide(Long rideId, Long clientId) {
        RideEntity rideEntity = rideRepositoryService.getRide(rideId);
        Ride ride = rideMapper.toDomain(rideEntity);
        if (ride == null) {
            throw new NoSuchElementException("Поездка не найдена");
        }
        if (!ride.getClientId().equals(clientId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }
        ride.setRideStatus(RideStatus.CANCELLED);
    }

    @KafkaListener(topics = "ride-create-request", groupId = "driver-service")
    public void processClientRideRequest(String message) throws JsonProcessingException {

        System.out.println("запрос на создание");

        ClientRideCreatedEvent request = mapper.readValue(message, ClientRideCreatedEvent.class);
        Ride ride = dtoRideMapper.fromEvent(request);
        System.out.println("client id:");
        System.out.println(ride.getClientId());

        RideEntity rideEntity = rideMapper.toEntity(ride);
        RideEntity savedRideEntity = rideRepositoryService.saveRide(rideEntity);
        Ride savedRide = rideMapper.toDomain(savedRideEntity);

        System.out.println(savedRide.getId());

        String jsonResponse = mapper.writeValueAsString(savedRide);
        kafkaStringTemplate.send("find-driver", jsonResponse);
    }

    @KafkaListener(topics = "driver-ride-request", groupId = "driver-service")
    public void processDriverRideRequest(String message) throws JsonProcessingException {
        System.out.println("Пришло в топик поиска поездокууууу");

        DriverRideRequestEvent request = mapper.readValue(message, DriverRideRequestEvent.class);

        double lon = request.getCurrentLongitude();
        double lat = request.getCurrentLatitude();
        Long driverId = request.getDriverId();
        System.out.println("Данные из события: lon=" + lon + ", lat=" + lat + ", driverId=" + driverId);

        List<Ride> rides = rideRepositoryService.getAllRides();

        List<Ride> requetsedRides = rides.stream()
                                        .filter(ride -> ride.getRideStatus() == RideStatus.REQUESTED)
                                        .collect(Collectors.toList());
        System.out.println("Отфильтровано поездок с статусом REQUESTED: " + requetsedRides.size());

        List<Ride> sortedRides = requetsedRides.stream()
                                     .sorted(Comparator.comparingDouble(ride -> servicesUtil.calculateDistance(ride.getStartLatitude(), ride.getStartLongitude(), lat, lon)))
                                     .collect(Collectors.toList());

        if (sortedRides.size() > 0) {
            Ride ride = sortedRides.get(0);
            System.out.println("назначена поездка с id: " + ride.getId());

            ride.setDriverId(driverId);
            ride.setRideStatus(RideStatus.ASSIGNED);
            RideEntity rideEntity = rideMapper.toEntity(ride);

            rideRepositoryService.saveRide(rideEntity);

            DriverStatusChangedEvent driverStatusChangedEvent = DriverStatusChangedEvent.builder()
                                                                    .driverId(driverId)
                                                                    .newStatus(DriverStatus.BUSY)
                                                                    .build();

            if (RideStatus.ASSIGNED.equals(ride.getRideStatus())) {
                kafkaStringTemplate.send("ride-assigned", String.valueOf(ride.getId()));
            }

            String jsonResponse = mapper.writeValueAsString(driverStatusChangedEvent);
            kafkaStringTemplate.send("driver-status", jsonResponse);
            System.out.println("Отправлено событие изменения статуса водителя");
        } else {
            System.out.println("Нет доступных поездок для назначения");
        }
    }

    @KafkaListener(topics = "ride-assigned", groupId = "driver-service")
    public void processRideAssigned(String message) throws JsonProcessingException {
        Long rideId = Long.parseLong(message);
        Ride ride = rideMapper.toDomain(rideRepositoryService.getRide(rideId));
        double amount = servicesUtil.calculateDistance(
                            ride.getStartLatitude(),
                            ride.getStartLongitude(),
                            ride.getEndLatitude(),
                            ride.getEndLongitude()) *
                        pricePerKm;
        System.out.println("amount:");
        System.out.println(amount);

        PaymentEvent paymentEvent = PaymentEvent.builder()
                                        .rideId(ride.getId())
                                        .clientId(ride.getClientId())
                                        .amount(amount)
                                        .build();

        String jsonResponse = mapper.writeValueAsString(paymentEvent);

        System.out.println("заявка в топик отправлена");
        kafkaStringTemplate.send("payment", jsonResponse);
    }

    @KafkaListener(topics = "ride-status", groupId = "driver-service")
    public void processRideStatusChange(String message) throws JsonProcessingException {

        NewRideStatusEvent request = mapper.readValue(message, NewRideStatusEvent.class);
        Long rideId = request.getRideId();
        Long driverID = request.getDriverId();
        RideStatus rideStatus = request.getRideStatus();

        RideEntity rideEntity = rideRepositoryService.getRide(rideId);
        rideEntity.setRideStatus(rideStatus);
        if (!RideStatus.IN_PROGRESS.equals(rideStatus)) {
            rideEntity.setDriverId(driverID);
        }

        rideRepositoryService.saveRide(rideEntity);
        Ride newRide = getRide(rideId);

        if (RideStatus.ASSIGNED.equals(newRide.getRideStatus())) {
            kafkaStringTemplate.send("ride-assigned", String.valueOf(newRide.getId()));
        }
    }
}
