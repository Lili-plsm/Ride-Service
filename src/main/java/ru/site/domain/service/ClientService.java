package ru.site.domain.service;

import ru.site.datasource.model.Client;
import ru.site.domain.model.Ride;

public interface ClientService {
  void createRide(Ride ride);

  void saveClient(Client client);

  Long getClientIdByUserId(Long userId);
}
