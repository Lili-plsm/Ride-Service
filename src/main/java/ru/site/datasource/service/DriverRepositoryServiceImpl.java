package ru.site.datasource.service;

import java.util.List;
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
        return optObj.orElseThrow(() -> new RuntimeException("Водитель не найден")).getId();
    }

    //@Cacheable("driversCache")
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    //@Cacheable(value = "driverCache", key = "#id")
    public Driver getDriver(Long id) {
        return driverRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Водитель не найден"));
    }

    //@CachePut(value = "driverCache", key = "#driver.id")
    //@CacheEvict(value = "driversCache", allEntries = true)
    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
