package com.example.crud2.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;

    @NotNull(message = "Количество обязательно")
    private Integer quantity;

}
