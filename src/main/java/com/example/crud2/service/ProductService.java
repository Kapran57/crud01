package com.example.crud2.service;

import com.example.crud2.dto.ProductDto;
import com.example.crud2.dto.mapper.ProductMapper;
import com.example.crud2.entity.ProductEntity;
import com.example.crud2.exception.DuplicateProductNameException;
import com.example.crud2.exception.ProductDeletionException;
import com.example.crud2.exception.ProductNotFoundException;
import com.example.crud2.repository.OrderItemRepository;
import com.example.crud2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Создание товара: name={}, price={}", productDto.getName(), productDto.getPrice());

        try {
            if (productRepository.existsByName(productDto.getName())) {
                log.warn("Товар с таким названием уже существует: {}", productDto.getName());
                throw new DuplicateProductNameException(productDto.getName());
            }

            ProductEntity entity = productMapper.toEntity(productDto);
            ProductEntity savedEntity = productRepository.save(entity);

            log.info("Товар создан: id={}, name={}, price={}",
                    savedEntity.getId(), savedEntity.getName(), savedEntity.getPrice());
            return productMapper.toDto(savedEntity);

        } catch (DuplicateProductNameException e) {
            log.error("Ошибка создания товара: название уже существует", e);
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании товара: name={}", productDto.getName(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        log.info("Fetching product with id: {}", id);

        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return productMapper.toDto(entity);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product with id: {}", id);

        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!entity.getName().equals(productDto.getName()) &&
                productRepository.existsByName(productDto.getName())) {
            throw new DuplicateProductNameException(productDto.getName());
        }

        productMapper.updateEntity(entity, productDto);

        ProductEntity updatedEntity = productRepository.save(entity);

        log.info("Product updated with id: {}", updatedEntity.getId());
        return productMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Удаление товара: id={}", id);

        try {
            if (!productRepository.existsById(id)) {
                log.warn("Товар не найден для удаления: id={}", id);
                throw new ProductNotFoundException(id);
            }

            if (orderItemRepository.existsByProductId(id)) {
                log.warn("Попытка удалить товар, который используется в заказах: id={}", id);
                throw new ProductDeletionException(id);
            }

            productRepository.deleteById(id);
            log.info("Товар удален: id={}", id);

        } catch (ProductNotFoundException | ProductDeletionException e) {
            log.error("Ошибка удаления товара: id={}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при удалении товара: id={}", id, e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        if (name != null && name.isBlank()) {
            name = null;
        }

        log.info("Fetching all products with filters - name: {}, minPrice: {}, maxPrice: {}, page: {}, size: {}",
                name, minPrice, maxPrice, page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductEntity> productsPage = productRepository.findByFilters(name, minPrice, maxPrice, pageable);

        return productsPage.map(productMapper::toDto);
    }
}
