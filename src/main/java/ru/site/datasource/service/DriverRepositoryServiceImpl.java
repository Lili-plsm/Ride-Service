package ru.site.datasource.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.Driver;
import ru.site.datasource.repository.DriverRepository;

@Service
public class DriverRepositoryServiceImpl implements DriverRepositoryService {

  private final DriverRepository driverRepository;

  public DriverRepositoryServiceImpl(DriverRepository driverRepository) {
    this.driverRepository = driverRepository;
  }

  public Long getDriverIdByUserId(Long userId) {
    Optional<Driver> optObj = driverRepository.findByUserId(userId);
    return optObj.orElseThrow(() -> new NoSuchElementException("Водитель не найден")).getId();
  }

  public List<Driver> getAllDrivers() {
    return driverRepository.findAll();
  }

  public Driver getDriver(Long id) {
    return driverRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("Водитель не найден"));
  }

  public Driver saveDriver(Driver driver) {
    return driverRepository.save(driver);
  }
}
