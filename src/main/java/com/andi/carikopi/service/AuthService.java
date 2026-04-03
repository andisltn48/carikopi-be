package com.andi.carikopi.service;

import com.andi.carikopi.dto.AuthResponse;
import com.andi.carikopi.dto.UserRequest;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.entity.CoffeeShop;
import com.andi.carikopi.entity.User;
import com.andi.carikopi.repository.CoffeeShopRepository;
import com.andi.carikopi.repository.UserRepository;
import com.andi.carikopi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CoffeeShopRepository coffeeShopRepository;
    @Autowired private JwtUtil jwtUtil;

    public WebResponse<String> register(UserRequest request){
        //check username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());

        //hash password before input
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(2);

        CoffeeShop coffeeShop = new CoffeeShop();
        coffeeShop.setUser(user);

        userRepository.save(user);
        coffeeShopRepository.save(coffeeShop);

        String msg = "User created successfully";
        return WebResponse.<String>builder()
                .code(200)
                .status("OK")
                .data(msg)
                .build();
    }

    public WebResponse<AuthResponse> login(UserRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("userNotFound"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            String token = jwtUtil.generateToken(user.getUsername());
            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .role(user.getRole())
                    .build();
            return WebResponse.<AuthResponse>builder()
                    .code(200)
                    .status("OK")
                    .data(authResponse)
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong credintial");
    }
}
