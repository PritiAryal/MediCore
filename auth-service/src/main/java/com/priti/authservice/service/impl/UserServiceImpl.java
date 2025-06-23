package com.priti.authservice.service.impl;

import com.priti.authservice.model.User;
import com.priti.authservice.repository.UserRepository;
import com.priti.authservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

     @Override
     public Optional<User> findByEmail(String email) {
         return userRepository.findByEmail(email);
     }
}
