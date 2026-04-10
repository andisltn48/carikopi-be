package com.andi.carikopi.feature.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XenditPaymentRequest {
    private String externalId;
    private Integer amount;
    private String description;
}
