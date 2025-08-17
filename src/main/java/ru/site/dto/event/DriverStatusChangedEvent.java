package ru.site.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.site.datasource.enums.DriverStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusChangedEvent {
  private Long driverId;
  private Long rideId;
  private DriverStatus newStatus;
}
