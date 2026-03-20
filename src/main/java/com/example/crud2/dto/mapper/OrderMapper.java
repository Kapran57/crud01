package com.example.crud2.dto.mapper;

import com.example.crud2.dto.ClientDto;
import com.example.crud2.dto.OrderDto;
import com.example.crud2.entity.OrderEntity;
import com.example.crud2.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "client", source = "client", qualifiedByName = "mapClientToDto")
    @Mapping(target = "clientId", source = "client.id")
    OrderDto toDto(OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "status", source = "status")
    OrderEntity toEntity(OrderDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "status", source = "status")
    void updateEntity(@MappingTarget OrderEntity entity, OrderDto dto);

    @Named("mapClientToDto")
    default ClientDto mapClientToDto(ClientEntity client) {
        if (client == null) return null;

        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        return dto;
    }
}