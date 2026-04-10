package com.andi.carikopi.feature.payment;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface XenditPaymentRepository extends JpaRepository<XenditPayment, UUID> {
    Optional<XenditPayment> findByOrderId(UUID orderId);
}
