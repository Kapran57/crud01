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

        if (productRepository.existsByName(productDto.getName())) {
            throw new DuplicateProductNameException(productDto.getName());
        }


        ProductEntity entity = productMapper.toEntity(productDto);


        ProductEntity savedEntity = productRepository.save(entity);

        log.info("Product created with id: {}", savedEntity.getId());
        return productMapper.toDto(savedEntity);
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
        log.info("Deleting product with id: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        if (orderItemRepository.existsByProductId(id)) {
            throw new ProductDeletionException(id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted with id: {}", id);
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

        log.info("Fetching all products with filters - name: {}, minPrice: {}, maxPrice: {}, page: {}, size: {}",
                name, minPrice, maxPrice, page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductEntity> productsPage = productRepository.findByFilters(name, minPrice, maxPrice, pageable);

        return productsPage.map(productMapper::toDto);
    }
}
