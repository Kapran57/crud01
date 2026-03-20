package com.example.crud2.dto.mapper;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.dto.OrderDto;
import com.example.crud2.entity.ClientEntity;
import com.example.crud2.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "orders", source = "orders", qualifiedByName = "mapOrdersToDto")
    ClientDto toDto(ClientEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    ClientEntity toEntity(ClientDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")

    void updateEntity(@MappingTarget ClientEntity entity, ClientDto dto);

    @Named("mapOrdersToDto")
    default List<OrderDto> mapOrdersToDto(List<OrderEntity> orders) {
        if (orders == null) return null;

        return orders.stream()
                .map(this::mapOrderToDto)
                .collect(Collectors.toList());
    }

    default OrderDto mapOrderToDto(OrderEntity order) {
        if (order == null) return null;

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setClientId(order.getClient() != null ? order.getClient().getId() : null);
        return dto;
    }
}