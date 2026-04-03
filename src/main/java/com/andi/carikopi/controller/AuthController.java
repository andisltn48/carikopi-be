package com.andi.carikopi.controller;

import com.andi.carikopi.dto.AuthResponse;
import com.andi.carikopi.dto.UserRequest;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthService authService;

    @PostMapping("/register")
    public WebResponse<String> register(@RequestBody UserRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public WebResponse<AuthResponse> login(@RequestBody UserRequest request){
        return authService.login(request);
    }
}
