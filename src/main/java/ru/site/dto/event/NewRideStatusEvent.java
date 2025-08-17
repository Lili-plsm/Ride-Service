package ru.site.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.site.datasource.enums.RideStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewRideStatusEvent {
  private Long rideId;
  private Long driverId;
  private RideStatus rideStatus;
}
