package ru.site.dto.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRideRequestEvent {
  private Long driverId;
  private double currentLatitude;
  private double currentLongitude;
}
