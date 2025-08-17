package ru.site.web.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.site.domain.service.RideService;
import ru.site.domain.service.UserService;

@RestController
public class RideController {

  private final RideService rideService;
  private final UserService userService;

  public RideController(RideService rideService, UserService userService) {
    this.rideService = rideService;
    this.userService = userService;
  }

  @PatchMapping("/rides/{rideId}/cancel")
  public ResponseEntity<?> cancelRide(@PathVariable Long rideId) {
    Long clientId = userService.getCurrentUser().getId();
    rideService.cancelRide(rideId, clientId);
    return ResponseEntity.ok(Map.of("message", "Поездка отменена"));
  }
}
