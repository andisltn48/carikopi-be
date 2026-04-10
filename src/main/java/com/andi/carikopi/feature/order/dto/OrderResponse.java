package com.andi.carikopi.feature.order.dto;

import java.util.Date;
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
public class OrderResponse {
    private UUID id;
    private String name;
    private String phone;

    @JsonProperty("total_price")
    private Integer totalPrice;
    
    @JsonProperty("order_menus")
    private List<OrderMenuResponse> orderMenus;

    private String status;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("queue_number")
    private String queueNumber;

    @JsonProperty("order_type")
    private String orderType;
}
