import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.OrderItemDto;
import com.example.crud2.entity.OrderStatus;
import com.example.crud2.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderDto updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Page<OrderDto> orders = orderService.getAllOrders(
                status, startDate, endDate, productId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<OrderDto>> getOrdersByClientId(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<OrderDto> orders = orderService.getOrdersByClientId(clientId, page, size);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto> addItemsToOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody List<OrderItemDto> items) {
        OrderDto updatedOrder = orderService.addItemsToOrder(orderId, items);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/items/{productId}")
    public ResponseEntity<OrderDto> updateOrderItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        OrderDto updatedOrder = orderService.updateOrderItemQuantity(orderId, productId, quantity);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}/items/{productId}")
    public ResponseEntity<OrderDto> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long productId) {
        OrderDto updatedOrder = orderService.removeItemFromOrder(orderId, productId);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemDto> items = orderService.getOrderItems(orderId);
        return ResponseEntity.ok(items);
    }
}