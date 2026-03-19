package com.example.crud2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    private String lastName;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный формат email")
    private String email;

    @Size(max = 20, message = "Номер телефона не должен превышать 20 символов")
    private String phone;

}