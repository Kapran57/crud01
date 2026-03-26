package com.example.crud2.dto;

import com.example.crud2.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO заказа")
public class OrderDto {

    @Schema(description = "Идентификатор заказа", example = "1")
    private Long id;

    @Schema(description = "Время создание заказа")
    private LocalDateTime createdAt;

    @NotNull(message = "Статус обязателен")
    @Schema(description = "Статус заказа", example ="Новый")
    private OrderStatus status;

    private ClientDto client;

    private Long clientId;

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();
}