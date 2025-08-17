package ru.site.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.site.datasource.enums.RideStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

  private Long id;
  private Long driverId;
  private Long clientId;

  private RideStatus rideStatus;

  private Double startLatitude;
  private Double startLongitude;
  private Double endLatitude;
  private Double endLongitude;

  private LocalDateTime createdAt;
}
