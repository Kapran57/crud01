package com.example.crud2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String description;

    @NotBlank(message = "Название товара обязательно")
    private String name;

    @NotNull(message = "Цена обязательна")
    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
