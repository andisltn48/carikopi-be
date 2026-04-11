package com.andi.carikopi.feature.coffeeshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XenditApiResponse {
    @JsonProperty("xendit_api_key")
    private String xenditApiKey;

    @JsonProperty("xendit_callback_token")
    private String xenditCallbackToken;
}
