package ru.site.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.Client;
import ru.site.datasource.service.ClientRepositoryService;
import ru.site.domain.model.Ride;
import ru.site.dto.event.ClientRideCreatedEvent;

@Service
public class ClientServiceImpl implements ClientService {

  private final ClientRepositoryService clientRepositoryService;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public ClientServiceImpl(
      KafkaTemplate<String, String> kafkaTemplate,
      ClientRepositoryService clientRepositoryService,
      ObjectMapper objectMapper) {

    this.kafkaTemplate = kafkaTemplate;
    this.clientRepositoryService = clientRepositoryService;
    this.objectMapper = objectMapper;
  }

  public void saveClient(Client client) {
    clientRepositoryService.saveClient(client);
  }

  public Long getClientIdByUserId(Long userId) {
    return clientRepositoryService.getClientIdByUserId(userId);
  }

  public void createRide(Ride ride) {
    ClientRideCreatedEvent event =
        ClientRideCreatedEvent.builder()
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
    try {
      String rideRequestJson = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("ride-create-request", rideRequestJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Ошибка парсинга jsom" + e.getMessage());
    }
  }
}
