package ru.site.datasource.mapper;

import org.springframework.stereotype.Component;
import ru.site.datasource.model.Driver;
import ru.site.datasource.model.User;
import ru.site.web.model.DriverCreateRequest;

@Component
public class DriverMapper {

    public Driver toEntity(DriverCreateRequest request, User user) {
        Driver driver = new Driver();
        driver.setUser(user);
        driver.setCarModel(request.getCarModel());
        driver.setCarNumber(request.getCarNumber());
        driver.setStatus(request.getStatus());
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());
        driver.setRating(0.0);
        return driver;
    }
}
