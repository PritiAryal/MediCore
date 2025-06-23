package com.priti.authservice.service;

import com.priti.authservice.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);
}
