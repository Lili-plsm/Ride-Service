package ru.site.web.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientCreateRequest {
    private Long userId;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(max = 50, message = "Имя не может быть длиннее 50 символов")
    private String firstName;

    @Size(max = 50, message = "Фамилия не может быть длиннее 50 символов")
    private String lastName;

    @Pattern(regexp = "\\+?[0-9]{10,15}",
             message = "Неверный формат номера телефона")
    private String phoneNumber;

    @Email(message = "Неверный формат email")
    private String email;
}
