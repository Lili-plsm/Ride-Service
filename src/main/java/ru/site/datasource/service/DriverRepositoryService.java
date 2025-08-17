package ru.site.datasource.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.Driver;

@Service
public interface DriverRepositoryService {
    public List<Driver> getAllDrivers();
    public Driver saveDriver(Driver driver);
    public Driver getDriver(Long id);
    public Long getDriverIdByUserId(Long userId);
}
