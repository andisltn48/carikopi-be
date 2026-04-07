package com.andi.carikopi.feature.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAllByUniqueSessionAndShopIdOrderByCreatedAtAsc(String uniqueSession, UUID shopId);
    List<Order> findAllByShopIdOrderByCreatedAtAsc(UUID shopId);
    List<Order> findAllByShopIdAndOrderNumberContainingIgnoreCaseOrderByCreatedAtAsc(UUID shopId, String orderNumber);
}
