package com.app.services.impl;

import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.exceptions.UserExistException;
import com.app.repository.UserRepository;

import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public DTONewUser saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            User userFromDB = userRepository.save(newUser);
            DTONewUser dtoNewUser = new DTONewUser();
            dtoNewUser.setUserId(userFromDB.getId());
            dtoNewUser.setEmail(userFromDB.getUsername());

            return dtoNewUser;
        } catch (Exception e) {
            throw new UserExistException("Username '" + newUser.getUsername() + "' already exists");
        }
    }

}
