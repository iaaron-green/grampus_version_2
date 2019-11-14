package com.app.services;

import com.app.entities.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    UserRepository userRepository;

    public void userRegister(User newUser){
        userRepository.save(newUser);
    }

    public User loadUserByUsername(String username){
      return userRepository.findByUsername(username);
    }
}
