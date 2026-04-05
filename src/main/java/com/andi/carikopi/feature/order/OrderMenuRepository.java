package com.andi.carikopi.feature.order;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {

}
