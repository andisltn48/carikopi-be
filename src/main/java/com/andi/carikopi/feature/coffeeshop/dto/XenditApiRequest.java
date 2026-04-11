package com.andi.carikopi.feature.coffeeshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class XenditApiRequest {
    @JsonProperty("xendit_api_key")
    private String xenditApiKey;

    @JsonProperty("xendit_callback_token")
    private String xenditCallbackToken;
}
