package ru.site.dto.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.site.datasource.enums.RideStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRideCreatedEvent {
  private Long id;
  private Long driverId;
  private Long clientId;
  private RideStatus rideStatus;
  private Double startLatitude;
  private Double startLongitude;
  private Double endLatitude;
  private Double endLongitude;
  private Long paymentId;
  private LocalDateTime createdAt;
}
