package com.example.crud2.repository;
import com.example.crud2.entity.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    Optional<OrderItemEntity> findByOrderIdAndProductId(Long orderId, Long productId);

    List<OrderItemEntity> findByOrderId(Long orderId);

    Page<OrderItemEntity> findByOrderId(Long orderId, Pageable pageable);

    boolean existsByProductId(Long productId);

    @Query("SELECT oi.product.id, oi.product.name, oi.product.price, oi.quantity " +
            "FROM OrderItemEntity oi " +
            "WHERE oi.order.id = :orderId")
    List<Object[]> findProductDetailsByOrderId(@Param("orderId") Long orderId);

    void deleteByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItemEntity oi WHERE oi.order.id = :orderId")
    Integer getTotalItemsByOrderId(@Param("orderId") Long orderId);
}
