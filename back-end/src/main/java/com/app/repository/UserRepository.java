package com.app.repository;

import org.springframework.security.core.userdetails.User;

public interface UserRepository {
    User findByUsername(String username);
    User getById(Long id);
}
