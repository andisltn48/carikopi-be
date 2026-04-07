package com.andi.carikopi.feature.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAllByUniqueSessionAndShopIdOrderByCreatedAtAsc(String uniqueSession, UUID shopId);
    Page<Order> findAllByShopIdOrderByCreatedAtAsc(UUID shopId, Pageable pageable);
    Page<Order> findAllByShopIdAndOrderNumberContainingIgnoreCaseOrderByCreatedAtAsc(UUID shopId, String orderNumber, Pageable pageable);
}
