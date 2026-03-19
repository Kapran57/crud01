package com.example.crud2.dto;

import com.example.crud2.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private LocalDateTime createdAt;

    @NotNull(message = "Статус обязателен для заполнения")
    private OrderStatus status;

    private UserDto user;
    private Long userId;
}