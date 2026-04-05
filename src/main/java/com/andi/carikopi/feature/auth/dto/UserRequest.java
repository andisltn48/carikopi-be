package com.andi.carikopi.feature.auth.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
}
