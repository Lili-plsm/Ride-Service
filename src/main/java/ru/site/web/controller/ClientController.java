package ru.site.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.site.datasource.mapper.ClientMapper;
import ru.site.datasource.model.Client;
import ru.site.datasource.model.User;
import ru.site.domain.model.Ride;
import ru.site.domain.service.ClientService;
import ru.site.domain.service.RideService;
import ru.site.domain.service.UserService;
import ru.site.web.mapper.RideWebMapper;
import ru.site.web.model.ClientCreateRequest;
import ru.site.web.model.RideRequest;
import ru.site.web.model.RideResponse;

@RestController
public class ClientController {

  private final ClientService clientService;
  private final RideService rideService;
  private final ClientMapper clientMapper;
  private final UserService userService;
  private final RideWebMapper rideWebMapper;

  public ClientController(
      ClientService clientService,
      UserService userService,
      ClientMapper clientMapper,
      RideWebMapper rideWebMapper,
      RideService rideService) {
    this.clientService = clientService;
    this.userService = userService;
    this.clientMapper = clientMapper;
    this.rideWebMapper = rideWebMapper;
    this.rideService = rideService;
  }

  private static final String DEFAULT_RIDE_DESCRIPTION = "Описание поездки";

  @GetMapping("/clients/rides/current")
  public ResponseEntity<?> getCurrentRideStatus() throws JsonProcessingException {

    Long clientId = userService.getCurrentUser().getId();

    Ride ride = rideService.getCurrentRideByClientId(clientId);

    RideResponse rideResponses = rideWebMapper.toResponse(ride, DEFAULT_RIDE_DESCRIPTION);

    if (ride == null) {
      return ResponseEntity.ok(Map.of("message", "нет текущий поездки"));
    } else return ResponseEntity.ok(rideResponses);
  }

  @PostMapping("/clients/rides")
  public ResponseEntity<?> createRide(@RequestBody RideRequest rideRequest)
      throws JsonProcessingException {
    Long clientId = userService.getCurrentUser().getId();
    Ride ride = rideWebMapper.fromRequest(rideRequest);
    ride.setClientId(clientId);
    clientService.createRide(ride);

    return ResponseEntity.ok(Map.of("message", "Запрос на поездку создан"));
  }

  @PostMapping("/clients")
  public ResponseEntity<?> createClient(@Valid @RequestBody ClientCreateRequest clientCreateRequest)
      throws JsonProcessingException {
    User user = userService.getCurrentUser();
    Client client = clientMapper.toEntity(clientCreateRequest, user);
    clientService.saveClient(client);
    return ResponseEntity.ok(Map.of("message", "Клиент создан"));
  }
}
