package ru.site.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.site.datasource.enums.DriverStatus;
import ru.site.datasource.enums.RideStatus;
import ru.site.datasource.model.Driver;
import ru.site.datasource.service.DriverRepositoryService;
import ru.site.domain.model.Ride;
import ru.site.dto.event.ClientRideCreatedEvent;
import ru.site.dto.event.DriverRideRequestEvent;
import ru.site.dto.event.DriverStatusChangedEvent;
import ru.site.dto.event.NewRideStatusEvent;
import ru.site.dto.mapper.DtoRideMapper;

@EnableKafka
@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepositoryService driverRepositoryService;
    private final KafkaTemplate<String, String> kafkaStringTemplate;
    private final ObjectMapper objectMapper;
    private final ServicesUtil servicesUtil;
    private final DtoRideMapper dtoRideMapper;

    @Autowired
    public DriverServiceImpl(
        DriverRepositoryService driverRepositoryService,
        @Qualifier("kafkaStringTemplate") KafkaTemplate<String, String> kafkaStringTemplate,
        ObjectMapper objectMapper,
        ServicesUtil servicesUtil,
        DtoRideMapper dtoRideMapper) {
        this.driverRepositoryService = driverRepositoryService;
        this.kafkaStringTemplate = kafkaStringTemplate;
        this.objectMapper = objectMapper;
        this.servicesUtil = servicesUtil;
        this.dtoRideMapper = dtoRideMapper;
    }

    public Driver getDriver(Long id) {
        return driverRepositoryService.getDriver(id);
    }

    public Long getDriverIdByUserId(Long userId) {
        return driverRepositoryService.getDriverIdByUserId(userId);
    }

    public void saveDriver(Driver driver) {
        driverRepositoryService.saveDriver(driver);
    }

    public void updateStatus(Driver driver, DriverStatus status) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is null");
        }

        driver.setStatus(status);

        ObjectNode json = objectMapper.createObjectNode();
        json.put("driverId", driver.getId());
        json.put("status", status.toString());
        driverRepositoryService.saveDriver(driver);

        kafkaStringTemplate.send("driver-status", json.toString());
    }

    public void processFindRide(Driver driver) {
        DriverRideRequestEvent driverRideRequestEvent = DriverRideRequestEvent.builder()
                                                            .driverId(driver.getId())
                                                            .currentLatitude(driver.getLatitude())
                                                            .currentLongitude(driver.getLongitude())
                                                            .build();
        try {
            String json = objectMapper.writeValueAsString(driverRideRequestEvent);
            kafkaStringTemplate.send("driver-ride-request",
                                     json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка парсинга");
        }
    }

    @KafkaListener(topics = "driver-status", groupId = "driver-service")
    public void processDriverStatusChange(String message) {
        DriverStatusChangedEvent statusChangedEvent = null;
        try{
            statusChangedEvent = objectMapper.readValue(message, DriverStatusChangedEvent.class);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException("Ошибка парсинга JSON");
        }

        Long driverId = statusChangedEvent.getDriverId();
        DriverStatus newStatus = statusChangedEvent.getNewStatus();

        Driver driver = driverRepositoryService.getDriver(driverId);
        driver.setStatus(newStatus);
        driverRepositoryService.saveDriver(driver);

        if (DriverStatus.WAITING.equals(driver.getStatus())) {
            DriverRideRequestEvent rideRequestEvent = DriverRideRequestEvent.builder()
                                                          .currentLatitude(driver.getLatitude())
                                                          .currentLongitude(driver.getLongitude())
                                                          .build();

            try {
                String eventJson = objectMapper.writeValueAsString(rideRequestEvent);
                kafkaStringTemplate.send("find-ride", eventJson);

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка парсинга");
            }
        }
    }

    @KafkaListener(topics = "find-driver", groupId = "driver-service")
    public void assignDriverToRide(String message){
		System.out.println("Пришел найти водителя");
        ClientRideCreatedEvent rideRequest = null;
        try{
            rideRequest = objectMapper.readValue(message, ClientRideCreatedEvent.class);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException("Ошибка парсинга JSON", e);
        }

        double startLatitude = rideRequest.getStartLatitude();
        double startLongitude = rideRequest.getStartLongitude();

        List<Driver> allDrivers = driverRepositoryService.getAllDrivers();
        List<Driver> freeDrivers = allDrivers.stream()
                                       .filter(driver -> driver.getStatus() == DriverStatus.FREE)
                                       .collect(Collectors.toList());

        List<Driver> sortedDrivers = freeDrivers.stream()
                                         .sorted(Comparator.comparingDouble(driver -> servicesUtil.calculateDistance(driver.getLatitude(), driver.getLongitude(), startLatitude, startLongitude)))
                                         .collect(Collectors.toList());

        Ride ride = dtoRideMapper.fromEvent(rideRequest);

        if (!sortedDrivers.isEmpty()) {
            Driver assignedDriver = sortedDrivers.get(0);
			assignedDriver.setStatus(DriverStatus.BUSY);
			driverRepositoryService.saveDriver(assignedDriver);
            ride.setDriverId(assignedDriver.getId());
            ride.setRideStatus(RideStatus.ASSIGNED);
			System.out.println(" нашел водителя");

            DriverStatusChangedEvent driverStatusChangedEvent = DriverStatusChangedEvent.builder()
                                                                    .driverId(assignedDriver.getId())
                                                                    .newStatus(DriverStatus.BUSY)
                                                                    .rideId(ride.getId())
                                                                    .build();

            try {
                String driverStatusJson = objectMapper.writeValueAsString(driverStatusChangedEvent);
                kafkaStringTemplate.send("driver-status", driverStatusJson);

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка парсинга");
            }

            NewRideStatusEvent newRideStatusEvent = NewRideStatusEvent.builder()
                                                        .rideId(ride.getId())
                                                        .rideStatus(RideStatus.ASSIGNED)
                                                        .driverId(assignedDriver.getId())
                                                        .build();

            try {
                String rideStatusJson = objectMapper.writeValueAsString(newRideStatusEvent);
                kafkaStringTemplate.send("ride-status", rideStatusJson);

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка парсинга");
            }

        } else {
            ride.setRideStatus(RideStatus.REQUESTED);
        }
    }
}
