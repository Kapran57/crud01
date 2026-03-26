package com.example.crud2.controller;

import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.OrderItemDto;
import com.example.crud2.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Создать заказ",
            description = "Создает новый заказ и возвращает его данные"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto) {
        log.info("POST /api/orders - создание заказа для клиента id={}, статус={}",
                orderDto.getClientId(), orderDto.getStatus());
        try {
            OrderDto createdOrder = orderService.createOrder(orderDto);
            log.info("Заказ создан: id={}, clientId={}, статус={}",
                    createdOrder.getId(), createdOrder.getClientId(), createdOrder.getStatus());
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Ошибка создания заказа для клиента id={}", orderDto.getClientId(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Посмотреть заказ",
            description = "Возвращает заказ по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        log.debug("GET /api/orders/{} - запрос заказа", id);
        try {
            OrderDto order = orderService.getOrderById(id);
            log.debug("Заказ найден: id={}, clientId={}, статус={}",
                    order.getId(), order.getClientId(), order.getStatus());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Ошибка получения заказа id={}", id, e);
            throw e;
        }
    }

    @Operation(
            summary = "Удалить заказ",
            description = "Удаляет заказ по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно удален"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /api/orders/{} - удаление заказа", id);

        try {
            orderService.deleteOrder(id);
            log.info("Заказ удален: id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка удаления заказа id={}", id, e);
            throw e;
        }
    }

    @Operation(
            summary = "Список заказов",
            description = "Возвращает весь список клиентов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "20" +
                    "1", description = "Список заказов успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        log.debug("GET /api/orders - запрос списка всех заказов");

        try {
            List<OrderDto> orders = orderService.getAllOrdersSimple();
            log.debug("Найдено заказов: {}", orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Ошибка получения списка заказов", e);
            throw e;
        }
    }

    @Operation(
            summary = "Добавить товар в заказ",
            description = "Добовляет товар в заказ по id заказа"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto> addItemsToOrder(
            @PathVariable Long orderId,
            @RequestBody List<OrderItemDto> items) {

        log.info("POST /api/orders/{}/items - добавление товаров в заказ, количество позиций: {}",
                orderId, items.size());
        log.debug("Добавляемые товары: {}", items);

        try {
            OrderDto updatedOrder = orderService.addItemsToOrder(orderId, items);
            log.info("Товары добавлены в заказ id={}, обновленный статус: {}",
                    orderId, updatedOrder.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("Ошибка добавления товаров в заказ id={}", orderId, e);
            throw e;
        }
    }

    @Operation(
            summary = "Товар в заказе",
            description = "Вернуть товары в заказе по id товара"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Товар успешно возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getOrderItems(@PathVariable Long orderId) {
        log.debug("GET /api/orders/{}/items - запрос товаров заказа", orderId);

        try {
            List<OrderItemDto> items = orderService.getOrderItems(orderId);
            log.debug("В заказе id={} найдено товаров: {}", orderId, items.size());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Ошибка получения товаров заказа id={}", orderId, e);
            throw e;
        }
    }
}