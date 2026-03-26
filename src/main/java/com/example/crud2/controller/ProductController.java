package com.example.crud2.controller;
import com.example.crud2.dto.ProductDto;
import com.example.crud2.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Создать продукт",
            description = "Создает новый продукт и возвращает его данные"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("POST /api/product - создание товара: name={}, price={}",
                productDto.getName(), productDto.getPrice());
        try {
            ProductDto createdProduct = productService.createProduct(productDto);
            log.info("Товар создан: id={}, name={}, price={}",
                    createdProduct.getId(), createdProduct.getName(), createdProduct.getPrice());
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        }catch (Exception e) {
            log.error("Ошибка создания товара: name={}", productDto.getName(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Посмотреть продукт",
            description = "Возвращает продукт по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        log.debug("GET /api/products/{} - запрос заказа", id);
        try {
            ProductDto product = productService.getProductById(id);
            log.debug("Товар найден: id={}, name={}, price={}",
                    product.getId(), product.getName(), product.getPrice());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Ошибка получения товара: id={}", id, e);
            throw e;
        }
    }

    @Operation(
            summary = "Обновить продукт",
            description = "Обновляет продукт по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        log.info("PUT /api/products/{} - обновление товара: name={}, price={}",
                id, productDto.getName(), productDto.getPrice());
        try {
            ProductDto updatedProduct = productService.updateProduct(id, productDto);
            log.info("Товар обновлен: id={}, name={}, price={}",
                    updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getPrice());
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("Ошибка обновления товара: id={}, name={}", id, productDto.getName(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Удалить продукт",
            description = "Удаляет продукт по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт успешно удален"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - удаление товара", id);

        try {
            productService.deleteProduct(id);
            log.info("Товар удален: id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка удаления товара: id={}", id, e);
            throw e;
        }
    }

    @Operation(
            summary = "Список продуктов",
            description = "Возвращает весь список продуктов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Список продуктов успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.debug("GET /api/products - запрос списка товаров: page={}, size={}, sortBy={}, sortDir={}," +
                        " filters=[name={}, minPrice={}, maxPrice={}]",
            page, size, sortBy, sortDirection, name, minPrice, maxPrice);

        try {
            Page<ProductDto> products = productService.getAllProducts(
                    name, minPrice, maxPrice, page, size, sortBy, sortDirection);
            log.debug("Найдено товаров: {}", products.getTotalElements());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Ошибка получения списка товаров", e);
            throw e;
        }
    }
}