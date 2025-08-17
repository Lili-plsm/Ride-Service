package ru.site.domain.service;

import ru.site.datasource.enums.DriverStatus;
import ru.site.datasource.model.Driver;

public interface DriverService {
  void updateStatus(Driver driver, DriverStatus status);

  Driver getDriver(Long id);

  void saveDriver(Driver driver);

  Long getDriverIdByUserId(Long userId);

  void processFindRide(Driver driver);
}
