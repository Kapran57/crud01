package com.example.crud2.controller;

import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.OrderItemDto;
import com.example.crud2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrdersSimple();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto> addItemsToOrder(
            @PathVariable Long orderId,
            @RequestBody List<OrderItemDto> items) {
        OrderDto updatedOrder = orderService.addItemsToOrder(orderId, items);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemDto> items = orderService.getOrderItems(orderId);
        return ResponseEntity.ok(items);
    }
}