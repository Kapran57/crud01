package com.example.crud2.unit;

import com.example.crud2.dto.ProductDto;
import com.example.crud2.dto.mapper.ProductMapper;
import com.example.crud2.entity.ProductEntity;
import com.example.crud2.exception.DuplicateProductNameException;
import com.example.crud2.exception.ProductDeletionException;
import com.example.crud2.exception.ProductNotFoundException;
import com.example.crud2.repository.OrderItemRepository;
import com.example.crud2.repository.ProductRepository;
import com.example.crud2.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductService productService;

    @Test
    void createProduct_success() {

        ProductDto dto = ProductDto.builder()
                .name("Phone")
                .price(new BigDecimal("100"))
                .build();

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Phone");
        entity.setPrice(new BigDecimal("100"));

        when(productRepository.existsByName("Phone")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(dto);

        ProductDto result = productService.createProduct(dto);

        assertEquals("Phone", result.getName());
        assertEquals(new BigDecimal("100"), result.getPrice());

        verify(productRepository).save(entity);
    }

    @Test
    void createProduct_duplicateName_shouldThrowException() {

        ProductDto dto = ProductDto.builder()
                .name("Phone")
                .price(new BigDecimal("100"))
                .build();

        when(productRepository.existsByName("Phone")).thenReturn(true);
        assertThrows(DuplicateProductNameException.class, () -> {
            productService.createProduct(dto);
        });

        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_repositoryThrowsException() {
        ProductDto dto = ProductDto.builder()
                .name("Phone")
                .price(new BigDecimal("100"))
                .build();

        ProductEntity entity = new ProductEntity();

        when(productRepository.existsByName("Phone")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            productService.createProduct(dto);
        });
    }

    @Test
    void getProductById_success() {

        Long id = 1L;

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Phone");

        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productMapper.toDto(entity)).thenReturn(dto);

        ProductDto result = productService.getProductById(id);
        assertEquals(1L, result.getId());
        assertEquals("Phone", result.getName());

        verify(productRepository).findById(id);
        verify(productMapper).toDto(entity);
    }
    @Test
    void getProductById_notFound_shouldThrowException() {

        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(id);
        });

        verify(productRepository).findById(id);
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void updateProduct_nameNotChanged_success() {

        Long id = 1L;

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Phone");

        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(dto);

        ProductDto result = productService.updateProduct(id, dto);

        verify(productRepository, never()).existsByName(any());
        verify(productMapper).updateEntity(entity, dto);
        verify(productRepository).save(entity);
        assertEquals("Phone", result.getName());
    }

    @Test
    void updateProduct_nameChanged_unique_success() {
        Long id = 1L;

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Phone");

        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("Cat")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDto(entity)).thenReturn(dto);
        when(productRepository.existsByName("Cat")).thenReturn(false);

        ProductDto result = productService.updateProduct(id, dto);

        verify(productMapper).updateEntity(entity, dto);
        verify(productRepository).save(entity);
        verify(productRepository).existsByName("Cat");
        assertEquals("Cat", result.getName());
    }

    @Test
    void updateProduct_NotFoundReturnsFail() {
        Long id = 1L;

        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("Cat")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProduct(id, dto)
        );

        verify(productRepository).findById(id);
        verify(productRepository, never()).existsByName(any());
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).updateEntity(any(), any());
    }

    @Test
    void updateProduct_nameChanged_duplicateName_throwsException() {
        Long id = 1L;

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Phone");

        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("Cat")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productRepository.existsByName("Cat")).thenReturn(true);

        assertThrows(DuplicateProductNameException.class, () ->
                productService.updateProduct(id, dto)
        );

        verify(productRepository).findById(id);
        verify(productRepository).existsByName("Cat");
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).updateEntity(any(), any());
    }

    @Test
    void deleteProduct_productExistsAndNotUsedInOrders_success() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(true);
        when(orderItemRepository.existsByProductId(id)).thenReturn(false);

        productService.deleteProduct(id);

        verify(productRepository).existsById(id);
        verify(orderItemRepository).existsByProductId(id);
        verify(productRepository).deleteById(id);
    }

    @Test
    void deleteProduct_productNotFound_throwsException() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () ->
                productService.deleteProduct(id)
        );

        verify(productRepository).existsById(id);
        verify(orderItemRepository, never()).existsByProductId(any());
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteProduct_productUsedInOrders_throwsProductDeletionException() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(true);
        when(orderItemRepository.existsByProductId(id)).thenReturn(true);

        assertThrows(ProductDeletionException.class, () ->
                productService.deleteProduct(id)
        );

        verify(productRepository).existsById(id);
        verify(orderItemRepository).existsByProductId(id);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteProduct_unexpectedException_throwsException() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenThrow(new RuntimeException("Ошибка подключения к базе данных"));

        assertThrows(RuntimeException.class, () ->
                productService.deleteProduct(id)
        );

        verify(productRepository).existsById(id);
        verify(orderItemRepository, never()).existsByProductId(any());
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void getAllProducts_withoutFilters_success() {

        String name = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";

        ProductEntity entity1 = new ProductEntity();
        entity1.setId(1L);
        entity1.setName("Product 1");

        ProductEntity entity2 = new ProductEntity();
        entity2.setId(2L);
        entity2.setName("Product 2");

        List<ProductEntity> entities = List.of(entity1, entity2);
        Page<ProductEntity> entityPage = new PageImpl<>(entities, PageRequest.of(page, size), entities.size());

        ProductDto dto1 = ProductDto.builder().id(1L).name("Product 1").build();
        ProductDto dto2 = ProductDto.builder().id(2L).name("Product 2").build();

        when(productRepository.findByFilters(eq(name), eq(minPrice), eq(maxPrice), any(PageRequest.class)))
                .thenReturn(entityPage);
        when(productMapper.toDto(entity1)).thenReturn(dto1);
        when(productMapper.toDto(entity2)).thenReturn(dto2);

        Page<ProductDto> result = productService.getAllProducts(
                name, minPrice, maxPrice, page, size, sortBy, sortDirection
        );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Product 1", result.getContent().get(0).getName());
        assertEquals("Product 2", result.getContent().get(1).getName());

        verify(productRepository).findByFilters(eq(name), eq(minPrice), eq(maxPrice), any(PageRequest.class));
        verify(productMapper, times(2)).toDto(any(ProductEntity.class));
    }
}