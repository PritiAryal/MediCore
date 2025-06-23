package com.priti.authservice.service.impl;

import com.priti.authservice.dto.LoginRequestDTO;
import com.priti.authservice.model.User;
import com.priti.authservice.service.AuthService;
import com.priti.authservice.service.UserService;
import com.priti.authservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // find user by email -> check if password matches -> generate token
    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<String> token = userService.findByEmail(loginRequestDTO.getEmail()) //1. get user by using email that we received in login request
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword())) // 2. pass that result to filter function  and then it will check if password in login request matches the one that is stored for the user if it doesnt match it returns empty effectively ending change
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole())); //generates token using user's email and role. we have to create jwtutil class.

        return token;
    }
}
