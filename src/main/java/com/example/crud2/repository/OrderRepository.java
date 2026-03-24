package com.example.crud2.repository;

import com.example.crud2.entity.OrderEntity;
import com.example.crud2.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    Page<OrderEntity> findByClientId(Long clientId, Pageable pageable);

    Page<OrderEntity> findByOrderItemsProductId(Long productId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN o.orderItems oi " +
            "WHERE (:status IS NULL OR o.status = :status) " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "AND (:productId IS NULL OR oi.product.id = :productId)")
    Page<OrderEntity> findByFilters(@Param("status") OrderStatus status,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("productId") Long productId,
                                    Pageable pageable);

    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN o.orderItems oi " +
            "GROUP BY o.id " +
            "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC")
    Page<OrderEntity> findAllSortedByTotalItemsDesc(Pageable pageable);

    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN o.orderItems oi " +
            "GROUP BY o.id " +
            "ORDER BY COALESCE(SUM(oi.quantity), 0) ASC")
    Page<OrderEntity> findAllSortedByTotalItemsAsc(Pageable pageable);

    @Query("SELECT o, COALESCE(SUM(oi.quantity), 0) as totalItems FROM OrderEntity o " +
            "LEFT JOIN o.orderItems oi " +
            "GROUP BY o.id")
    Page<Object[]> findOrdersWithTotalItems(Pageable pageable);
}