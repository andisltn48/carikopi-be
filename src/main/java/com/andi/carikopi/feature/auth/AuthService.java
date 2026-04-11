package com.andi.carikopi.feature.auth;

import com.andi.carikopi.feature.auth.dto.AuthResponse;
import com.andi.carikopi.feature.auth.dto.UserRequest;
import com.andi.carikopi.common.WebResponse;
import com.andi.carikopi.feature.coffeeshop.CoffeeShop;
import com.andi.carikopi.feature.coffeeshop.CoffeeShopRepository;
import com.andi.carikopi.security.JwtUtil;

import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Transactional
    public WebResponse<String> register(UserRequest request){
        try {
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
            if (request.getRegisterToken() != null) {
                coffeeShop = coffeeShopRepository.findByRegisterToken(request.getRegisterToken())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee shop tidak ditemukan"));
                user.setRole(3);
            } else {
                String registerToken = UUID.randomUUID().toString();
                coffeeShop.setRegisterToken(registerToken);
                user.setRole(2);
            }

            user.setShop(coffeeShop);
            coffeeShopRepository.save(coffeeShop);
            userRepository.save(user);

            String msg = "User created successfully";
            return WebResponse.<String>builder()
                    .code(200)
                    .status("OK")
                    .data(msg)
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
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

    public WebResponse<Map<String, Object>> getPrivilege(Principal principal){
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("userNotFound"));
        Map<String, Object> privilege = new HashMap<>();
        privilege.put("role", user.getRole());
        privilege.put("username", user.getUsername());
        privilege.put("shop_id", user.getShop().getId());
        return WebResponse.<Map<String, Object>>builder()
                .code(200)
                .status("OK")
                .data(privilege)
                .build();
    }
}
