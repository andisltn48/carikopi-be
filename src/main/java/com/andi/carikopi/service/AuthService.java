package com.andi.carikopi.service;

import com.andi.carikopi.dto.UserRequest;
import com.andi.carikopi.dto.WebResponse;
import com.andi.carikopi.entity.User;
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
    @Autowired private JwtUtil jwtUtil;

    public WebResponse<String> register(UserRequest request){
        User user = new User();
        user.setUsername(request.getUsername());

        //hash password before input
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(2);
        userRepository.save(user);

        String msg = "User created successfully";
        return WebResponse.<String>builder()
                .code(200)
                .status("OK")
                .data(msg)
                .build();
    }

    public WebResponse<String> login(UserRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("userNotFound"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            String token = jwtUtil.generateToken(user.getUsername());
            return WebResponse.<String>builder()
                    .code(200)
                    .status("OK")
                    .data(token)
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong credintial");
    }
}
