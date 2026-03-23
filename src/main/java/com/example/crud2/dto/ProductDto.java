package com.example.crud2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String description;

    @NotBlank(message = "Название товара обязательно")
    private String name;

    @NotBlank(message = "Цена обязательна")
    private BigDecimal price;

}
