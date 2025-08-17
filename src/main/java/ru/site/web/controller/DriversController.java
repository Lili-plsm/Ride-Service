package ru.site.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.site.datasource.mapper.DriverMapper;
import ru.site.datasource.model.Driver;
import ru.site.datasource.model.User;
import ru.site.domain.model.Ride;
import ru.site.domain.service.DriverService;
import ru.site.domain.service.RideService;
import ru.site.domain.service.UserService;
import ru.site.web.mapper.RideWebMapper;
import ru.site.web.model.DriverCreateRequest;
import ru.site.web.model.RideResponse;

@RestController
public class DriversController {

    private static final String DEFAULT_RIDE_DESCRIPTION = "Описание поездки";

    private final UserService userService;
    private final DriverService driverService;
    private final RideService rideService;
    private final DriverMapper driverMapper;
    private final RideWebMapper rideWebMapper;

    public DriversController(UserService userService,
                             DriverService driverService,
                             DriverMapper driverMapper,
                             RideService rideService,
                             RideWebMapper rideWebMapper) {
        this.driverService = driverService;
        this.userService = userService;
        this.driverMapper = driverMapper;
        this.rideService = rideService;
        this.rideWebMapper = rideWebMapper;
    }

    @GetMapping("/drivers/rides/current")
    public ResponseEntity<?> getCurrentRideStatus() {
        Long userId = userService.getCurrentUser().getId();
		Long driverId = driverService.getDriverIdByUserId(userId);
        Ride ride = rideService.getCurrentRideByDriverId(driverId);
        RideResponse rideResponse = rideWebMapper.toResponse(ride, DEFAULT_RIDE_DESCRIPTION);
        if (ride == null) {
            return ResponseEntity.ok(Map.of("message", "нет текущих поездок"));
        } else
            return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/drivers")
    public ResponseEntity<?>
    createDriver(@Valid @RequestBody DriverCreateRequest driverCreateRequest) {

        User user = userService.getCurrentUser();
        Driver driver = driverMapper.toEntity(driverCreateRequest, user);
        driverService.saveDriver(driver);
        return ResponseEntity.ok(Map.of("message", "Водитель создан"));
    }

    @PostMapping("/drivers/rides")
    public ResponseEntity<?> findRide() throws JsonProcessingException {
        Long userId = userService.getCurrentUser().getId();
        Driver driver = driverService.getDriver(driverService.getDriverIdByUserId(userId));
        driverService.processFindRide(driver);
        return ResponseEntity.ok(Map.of("message", "Запрос на поездку создан"));
    }
}
