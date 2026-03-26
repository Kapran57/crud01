package com.example.crud2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO клиента")
public class ClientDto {

    @Schema(description = "Идентификатор клиента", example = "1")
    private Long id;

    @Schema(description = "Имя клиента", example = "Иван")
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @Schema(description = "Фамилия клиента", example = "Иванов")
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @Schema(description = "Email клиента", example = "ivan@mail.ru")
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Schema(description = "Телефон клиента", example = "+70000000000")
    @Size(max = 20)
    private String phone;

    @Schema(description = "Время создание клиента")
    private LocalDateTime createdAt;

    @Schema(description = "Время обновление клиента")
    private LocalDateTime updatedAt;

    private List<OrderDto> orders;
}