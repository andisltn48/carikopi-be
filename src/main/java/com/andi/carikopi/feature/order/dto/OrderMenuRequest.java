package com.andi.carikopi.feature.order.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderMenuRequest {
    private UUID menuId;
    private Integer quantity;
    private Integer totalPrice;
    private String notes;
}
