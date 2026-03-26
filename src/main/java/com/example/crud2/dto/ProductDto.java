package com.example.crud2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO продукта")
public class ProductDto {

    @Schema(description = "Идентификатор продукта", example = "1")
    private Long id;
    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Имя продукта", example = "Телевизор")
    @NotBlank(message = "Название товара обязательно")
    private String name;

    @Schema(description = "Цена продукта", example = "1000.00")
    @NotNull(message = "Цена обязательна")
    private BigDecimal price;

    @Schema(description = "Время создание продукта")
    private LocalDateTime createdAt;

    @Schema(description = "Время обновление продукта")
    private LocalDateTime updatedAt;

}
