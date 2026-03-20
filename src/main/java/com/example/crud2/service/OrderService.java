package com.example.crud2.service;

import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.mapper.OrderMapper;
import com.example.crud2.entity.OrderEntity;
import com.example.crud2.entity.OrderStatus;
import com.example.crud2.entity.ClientEntity;
import com.example.crud2.exception.*;
import com.example.crud2.repository.OrderRepository;
import com.example.crud2.repository.ClientRepository;
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
    private final ClientRepository clientRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        ClientEntity client = clientRepository.findById(orderDto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(orderDto.getClientId()));

        OrderEntity entity = orderMapper.toEntity(orderDto);

        entity.setClient(client);

        OrderEntity savedEntity = orderRepository.save(entity);

        OrderDto resultDto = orderMapper.toDto(savedEntity);
        resultDto.setClientId(client.getId());

        return resultDto;
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toDto(entity);
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

        return orderMapper.toDto(updatedEntity);
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
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderEntity> ordersPage = orderRepository.findByFilters(status, startDate, endDate, pageable);

        return ordersPage.map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByClientId(Long clientId, int page, int size) {
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
}