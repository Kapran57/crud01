package com.example.crud2.dto.mapper;

import com.example.crud2.dto.OrderItemDto;
import com.example.crud2.entity.OrderItemEntity;
import com.example.crud2.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    OrderItemDto toDto(OrderItemEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProduct")
    OrderItemEntity toEntity(OrderItemDto dto);

    @Named("mapProduct")
    default ProductEntity mapProduct(Long productId) {
        if (productId == null) return null;
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        return product;
    }
}