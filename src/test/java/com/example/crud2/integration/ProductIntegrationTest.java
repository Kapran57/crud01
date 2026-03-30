package com.example.crud2.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.example.crud2.dto.ProductDto;
import com.example.crud2.entity.ProductEntity;
import com.example.crud2.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Commit
class ProductIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private static final String BASE_URL = "/api/products";

    @Test
    void shouldCreateProduct() {
        ProductDto request = ProductDto.builder()
                .name("iPhone")
                .description("Apple phone")
                .price(new BigDecimal("999.99"))
                .build();

        ResponseEntity<ProductDto> response =
                restTemplate.postForEntity(BASE_URL, request, ProductDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("iPhone");

        List<ProductEntity> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("iPhone");
    }

    @Test
    void shouldGetProductById() {
        ProductEntity product = new ProductEntity();
        product.setName("Test");
        product.setPrice(BigDecimal.valueOf(100));

        ProductEntity saved = productRepository.saveAndFlush(product);
        Long id = saved.getId();

        ResponseEntity<ProductDto> response =
                restTemplate.getForEntity("/api/products/" + id, ProductDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldUpdateProduct() {
        ProductEntity entity = ProductEntity.builder()
                .name("СтароеИмя")
                .description("старое")
                .price(new BigDecimal("100"))
                .build();

        entity = productRepository.save(entity);

        ProductDto updateRequest = ProductDto.builder()
                .name("НовоеИмя")
                .description("Updated")
                .price(new BigDecimal("150"))
                .build();

        HttpEntity<ProductDto> requestEntity = new HttpEntity<>(updateRequest);

        ResponseEntity<ProductDto> response = restTemplate.exchange(
                BASE_URL + "/" + entity.getId(),
                HttpMethod.PUT,
                requestEntity,
                ProductDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("НовоеИмя");

        ProductEntity updated = productRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("НовоеИмя");
    }

    @Test
    void shouldDeleteProduct() {
        ProductEntity entity = ProductEntity.builder()
                .name("Delete")
                .description("del")
                .price(new BigDecimal("50"))
                .build();

        entity = productRepository.save(entity);

        restTemplate.delete(BASE_URL + "/" + entity.getId());

        assertThat(productRepository.findById(entity.getId())).isEmpty();
    }

    @Test
    void shouldGetAllProducts() {
        productRepository.save(ProductEntity.builder()
                .name("A")
                .price(new BigDecimal("10"))
                .build());

        productRepository.save(ProductEntity.builder()
                .name("B")
                .price(new BigDecimal("20"))
                .build());

        ResponseEntity<String> response =
                restTemplate.getForEntity(BASE_URL, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("A");
        assertThat(response.getBody()).contains("B");
    }
}