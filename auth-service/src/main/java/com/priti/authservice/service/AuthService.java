package com.priti.authservice.service;

import com.priti.authservice.dto.LoginRequestDTO;
import com.priti.authservice.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AuthService {

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO);
    public boolean validateToken(String token);
}
