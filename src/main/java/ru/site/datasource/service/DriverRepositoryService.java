package ru.site.datasource.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.Driver;

@Service
public interface DriverRepositoryService {
  List<Driver> getAllDrivers();

  Driver saveDriver(Driver driver);

  Driver getDriver(Long id);

  Long getDriverIdByUserId(Long userId);
}
