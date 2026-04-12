package com.andi.carikopi.feature.order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<Order> belongsToShop(UUID shopId) {
        return (root, query, cb) -> cb.equal(root.get("shop").get("id"), shopId);
    }

    public static Specification<Order> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> orderNumberContains(String keyword) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("orderNumber")),
                "%" + keyword.toLowerCase() + "%"
        );
    }

    public static Specification<Order> orderByCreatedAtAsc() {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("createdAt")));
            return cb.conjunction();
        };
    }

    public static Specification<Order> uniqueSessionContains(String uniqueSession) {
        return (root, query, cb) -> cb.equal(root.get("uniqueSession"), uniqueSession);
    }

    public static Specification<Order> createdAtBetween(Date startDate, Date endDate) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), startDate, endDate);
    }

    public static Specification<Order> orderByCreatedAtDesc() {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.conjunction();
        };
    }

    public static Specification<Order> orderCreatedToday() {
        return (root, query, cb) -> {
            LocalDate today = LocalDate.now();
            
            // Konversi LocalDate ke java.util.Date untuk awal hari ini
            Date start = Date.from(today.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            
            // Konversi ke awal besok hari
            Date end = Date.from(today.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            
            return cb.between(root.get("createdAt"), start, end);
        };
    }

    public static Specification<Order> orderCreatedThisMonth() {
        return (root, query, cb) -> {
            LocalDate today = LocalDate.now();
            LocalDate firstDay = today.withDayOfMonth(1);
            LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());
            
            Date start = Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(lastDay.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
            
            return cb.between(root.get("createdAt"), start, end);
        };
    }

    public static Specification<Order> orderCompleted() {
        return (root, query, cb) -> cb.equal(root.get("status"), "DONE");
    }
}
