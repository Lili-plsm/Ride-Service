package ru.site.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.site.datasource.enums.RideStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {
    private RideStatus rideStatus;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
}
