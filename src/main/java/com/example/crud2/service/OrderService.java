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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Создание нового заказа для клиента id: {}", orderDto.getClientId());

        ClientEntity client = clientRepository.findById(orderDto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(orderDto.getClientId()));

        OrderEntity entity = orderMapper.toEntity(orderDto);
        entity.setClient(client);
        entity.setStatus(OrderStatus.NEW);

        OrderEntity savedEntity = orderRepository.save(entity);

        log.info("Заказ созданный с помощью id: {}", savedEntity.getId());
        return orderMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        log.info("Получение заказа с помощью id: {}", id);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toDto(entity);
    }

    @Transactional
    public void deleteOrder(Long id) {
        log.info("Удаление заказа с помощью id: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }

        orderRepository.deleteById(id);
        log.info("Заказ удален с помощью id: {}", id);
    }

    public List<OrderDto> getAllOrdersSimple() {
        log.info("Получение всех заказов");
        List<OrderEntity> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }

    @Transactional
    public OrderDto addItemsToOrder(Long orderId, List<OrderItemDto> itemsDto) {
        log.info("Добавление товаров в заказ с помощью id: {}", orderId);

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
                log.debug("Количество продукта {} в заказе {} на {}",
                        product.getId(), orderId, existingItem.getQuantity());
            } else {
                OrderItemEntity newItem = OrderItemEntity.builder()
                        .quantity(itemDto.getQuantity())
                        .product(product)
                        .order(order)
                        .build();
                order.addOrderItem(newItem);
                log.debug("Добавлен новый товар {} в заказ {} с указанием количества {}",
                        product.getId(), orderId, itemDto.getQuantity());
            }
        }

        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Успешно добавлены {} товары в заказ {}", itemsDto.size(), orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItems(Long orderId) {
        log.info("Получение товаров для заказа: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }

        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);

        return items.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }
}