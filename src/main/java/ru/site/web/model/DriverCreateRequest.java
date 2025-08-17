package ru.site.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.site.datasource.enums.DriverStatus;

@Data
public class DriverCreateRequest {
    private Long userId;

    @NotBlank(message = "Модель автомобиля обязательна")
    @Size(max = 50,
          message = "Модель автомобиля не может быть длиннее 50 символов")
    private String carModel;

    @NotBlank(message = "Госномер автомобиля обязателен")
    @Pattern(regexp = "^[А-ЯA-Z0-9-]{5,12}$",
             message = "Неверный формат госномера")
    private String carNumber;

    private DriverStatus status = DriverStatus.FREE;

    private Double latitude;

    private Double longitude;
}
