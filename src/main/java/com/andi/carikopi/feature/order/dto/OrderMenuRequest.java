package com.andi.carikopi.feature.order.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMenuRequest {
    @JsonProperty("menu_id")
    private UUID menuId;

    private Integer quantity;

    @JsonProperty("total_price")
    private Integer totalPrice;
    
    private String notes;
}
