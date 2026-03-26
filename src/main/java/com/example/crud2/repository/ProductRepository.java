package com.example.crud2.repository;

import com.example.crud2.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByName(String name);

    @Query(
            value = """
    select *
    from products p
    where (:name is null or p.name ilike concat('%', cast(:name as text), '%'))
      and (:minPrice is null or p.price >= :minPrice)
      and (:maxPrice is null or p.price <= :maxPrice)
    order by p.name
    """,
            countQuery = """
    select count(*)
    from products p
    where (:name is null or p.name ilike concat('%', cast(:name as text), '%'))
      and (:minPrice is null or p.price >= :minPrice)
      and (:maxPrice is null or p.price <= :maxPrice)
    """,
            nativeQuery = true
    )
    Page<ProductEntity> findByFilters(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
