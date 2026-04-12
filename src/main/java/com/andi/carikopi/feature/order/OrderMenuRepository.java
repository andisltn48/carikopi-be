package com.andi.carikopi.feature.order;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {

    @Query("SELECT om.menu.nama as name, SUM(om.quantity) as total " +
           "FROM OrderMenu om " +
           "WHERE om.order.shop.id = :shopId " +
           "AND om.order.status = 'DONE' " +
           "GROUP BY om.menu.id, om.menu.nama " +
           "ORDER BY SUM(om.quantity) DESC")
    List<Map<String, Object>> findTopSellingMenus(@Param("shopId") UUID shopId, Pageable pageable);
}
