package com.andi.carikopi.feature.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;

    @JsonProperty("register_token")
    private String registerToken;
}
