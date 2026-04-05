package com.andi.carikopi.feature.order.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {
    private String name;
    private String phone;
    private String note;

    @JsonProperty("total_price")
    private Integer totalPrice;
    
    @JsonProperty("order_menus")
    private List<OrderMenuRequest> orderMenus;
    
    @JsonProperty("shop_id")
    private UUID shopId;

    @JsonProperty("unique_session")
    private String uniqueSession;
}
