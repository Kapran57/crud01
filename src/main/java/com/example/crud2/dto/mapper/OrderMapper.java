package com.example.crud2.dto.mapper;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.dto.OrderDto;
import com.example.crud2.dto.OrderItemDto;
import com.example.crud2.entity.ClientEntity;
import com.example.crud2.entity.OrderEntity;
import com.example.crud2.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "client", source = "client", qualifiedByName = "mapClientToDto")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "items", source = "orderItems", qualifiedByName = "mapOrderItemsToDto")
    OrderDto toDto(OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "status", source = "status")
    OrderEntity toEntity(OrderDto dto);

    @Named("mapOrderItemsToDto")
    default List<OrderItemDto> mapOrderItemsToDto(List<OrderItemEntity> orderItems) {
        if (orderItems == null) return null;
        return orderItems.stream()
                .map(this::mapOrderItemToDto)
                .collect(Collectors.toList());
    }
    List<OrderDto> toDtoList(List<OrderEntity> entities);

    default OrderItemDto mapOrderItemToDto(OrderItemEntity orderItem) {
        if (orderItem == null) return null;
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null)
                .productName(orderItem.getProduct() != null ? orderItem.getProduct().getName() : null)
                .productPrice(orderItem.getProduct() != null ? orderItem.getProduct().getPrice() : null)
                .quantity(orderItem.getQuantity())
                .build();
    }

    @Named("mapClientToDto")
    default ClientDto mapClientToDto(ClientEntity client) {
        if (client == null) return null;
        return ClientDto.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .build();
    }
}