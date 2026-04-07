package com.andi.carikopi.feature.order.dto;

import java.util.UUID;

import com.andi.carikopi.feature.menu.Menu;
import com.andi.carikopi.feature.menu.dto.MenuResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMenuResponse {
    @JsonProperty("menu")
    private MenuResponse menu;

    private Integer quantity;

    @JsonProperty("total_price")
    private Integer totalPrice;
    
    private String notes;
}
