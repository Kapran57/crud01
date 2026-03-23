package com.example.crud2.service;

import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.OrderItemDto;
import com.example.crud2.dto.mapper.OrderItemMapper;
import com.example.crud2.dto.mapper.OrderMapper;
import com.example.crud2.entity.*;
import com.example.crud2.exception.*;
import com.example.crud2.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Creating new order for client id: {}", orderDto.getClientId());

        ClientEntity client = clientRepository.findById(orderDto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(orderDto.getClientId()));

        OrderEntity entity = orderMapper.toEntity(orderDto);
        entity.setClient(client);
        entity.setStatus(OrderStatus.NEW);

        OrderEntity savedEntity = orderRepository.save(entity);

        log.info("Order created with id: {}", savedEntity.getId());
        return orderMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toDto(entity);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        log.info("Updating order status for order id: {} to {}", id, status);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (entity.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusException(entity.getStatus(), status);
        }

        entity.setStatus(status);
        OrderEntity updatedEntity = orderRepository.save(entity);

        log.info("Order status updated for order id: {}", id);
        return orderMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted with id: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long productId,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        log.info("Fetching all orders with filters - status: {}, startDate: {}, endDate: {}, productId: {}, sortBy: {}, sortDirection: {}",
                status, startDate, endDate, productId, sortBy, sortDirection);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> ordersPage;

        if ("totalItems".equals(sortBy)) {
            if ("ASC".equalsIgnoreCase(sortDirection)) {
                ordersPage = orderRepository.findAllSortedByTotalItemsAsc(pageable);
            } else {
                ordersPage = orderRepository.findAllSortedByTotalItemsDesc(pageable);
            }
        } else {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            pageable = PageRequest.of(page, size, sort);
            ordersPage = orderRepository.findByFilters(status, startDate, endDate, productId, pageable);
        }

        return ordersPage.map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByClientId(Long clientId, int page, int size) {
        log.info("Fetching orders for client id: {}", clientId);

        if (!clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OrderEntity> ordersPage = orderRepository.findByClientId(clientId, pageable);

        return ordersPage.map(order -> {
            OrderDto dto = orderMapper.toDto(order);
            dto.setClientId(clientId);
            return dto;
        });
    }

    @Transactional
    public OrderDto addItemsToOrder(Long orderId, List<OrderItemDto> itemsDto) {
        log.info("Adding items to order with id: {}", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELED) {
            throw new InvalidOrderStatusException(order.getStatus(), null);
        }

        for (OrderItemDto itemDto : itemsDto) {
            ProductEntity product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemDto.getProductId()));

            if (itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Количество товара должно быть положительным");
            }

            OrderItemEntity existingItem = order.getOrderItems().stream()
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + itemDto.getQuantity());
                log.debug("Updated quantity for product {} in order {} to {}",
                        product.getId(), orderId, existingItem.getQuantity());
            } else {
                OrderItemEntity newItem = OrderItemEntity.builder()
                        .quantity(itemDto.getQuantity())
                        .product(product)
                        .order(order)
                        .build();
                order.addOrderItem(newItem);
                log.debug("Added new product {} to order {} with quantity {}",
                        product.getId(), orderId, itemDto.getQuantity());
            }
        }

        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Successfully added {} items to order {}", itemsDto.size(), orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrderItemQuantity(Long orderId, Long productId, Integer quantity) {
        log.info("Updating item quantity in order {} for product {} to {}", orderId, productId, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным");
        }

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELED) {
            throw new InvalidOrderStatusException(order.getStatus(), null);
        }

        OrderItemEntity orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(orderId, productId));

        orderItem.setQuantity(quantity);

        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Updated quantity for product {} in order {} to {}", productId, orderId, quantity);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto removeItemFromOrder(Long orderId, Long productId) {
        log.info("Removing item from order {} for product {}", orderId, productId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELED) {
            throw new InvalidOrderStatusException(order.getStatus(), null);
        }

        OrderItemEntity orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(orderId, productId));

        order.removeOrderItem(orderItem);

        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Removed product {} from order {}", productId, orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItems(Long orderId) {
        log.info("Getting items for order: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }

        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);

        return items.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getOrderTotalItems(Long orderId) {
        log.info("Getting total items count for order: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }

        return orderItemRepository.getTotalItemsByOrderId(orderId);
    }
}