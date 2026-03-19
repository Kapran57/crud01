package com.example.crud2.service;

import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.UserDto;
import com.example.crud2.entity.OrderEntity;
import com.example.crud2.entity.OrderStatus;
import com.example.crud2.entity.UserEntity;
import com.example.crud2.exception.*;
import com.example.crud2.repository.OrderRepository;
import com.example.crud2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private OrderDto convertToDto(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setStatus(entity.getStatus());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());

            UserDto userDto = new UserDto();
            userDto.setId(entity.getUser().getId());
            userDto.setFirstName(entity.getUser().getFirstName());
            userDto.setLastName(entity.getUser().getLastName());
            userDto.setEmail(entity.getUser().getEmail());
            dto.setUser(userDto);
        }

        return dto;
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        UserEntity user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(orderDto.getUserId()));

        OrderEntity entity = new OrderEntity();
        entity.setStatus(orderDto.getStatus());
        entity.setUser(user);

        OrderEntity savedEntity = orderRepository.save(entity);

        OrderDto resultDto = convertToDto(savedEntity);
        resultDto.setUserId(user.getId());

        return resultDto;
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return convertToDto(entity);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (entity.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusException(entity.getStatus(), status);
        }

        entity.setStatus(status);
        OrderEntity updatedEntity = orderRepository.save(entity);

        return convertToDto(updatedEntity);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);  // ← pageable создается правильно

        Page<OrderEntity> ordersPage = orderRepository.findByFilters(status, startDate, endDate, pageable);

        return ordersPage.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUserId(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OrderEntity> ordersPage = orderRepository.findByUserId(userId, pageable);

        return ordersPage.map(order -> {
            OrderDto dto = convertToDto(order);
            dto.setUserId(userId);
            return dto;
        });
    }
}