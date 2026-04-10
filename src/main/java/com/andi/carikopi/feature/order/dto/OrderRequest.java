package com.andi.carikopi.feature.order.dto;

import java.util.List;
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
public class OrderRequest {
    private String name;
    private String phone;

    @JsonProperty("total_price")
    private Integer totalPrice;
    
    @JsonProperty("order_menus")
    private List<OrderMenuRequest> orderMenus;
    
    @JsonProperty("shop_id")
    private UUID shopId;

    @JsonProperty("unique_session")
    private String uniqueSession;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("status")
    private String status;
}
