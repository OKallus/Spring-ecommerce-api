package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.entity.Order;
import com.ecommerce.ecommerce_api.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    @Query("SELECT o FROM Order o JOIN FETCH o.items i JOIN FETCH i.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}