package ru.site.web.model;

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
public class RideResponse {
  private Long rideId;
  private RideStatus status;
  private LocalDateTime createdAt;
  private String message;
}
