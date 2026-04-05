package com.andi.carikopi.feature.auth;

import com.andi.carikopi.feature.auth.dto.AuthResponse;
import com.andi.carikopi.feature.auth.dto.UserRequest;
import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.auth.AuthService;
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
