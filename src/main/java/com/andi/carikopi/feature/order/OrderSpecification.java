package com.andi.carikopi.feature.order;

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
}
