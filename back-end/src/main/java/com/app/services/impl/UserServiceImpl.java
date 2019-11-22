package com.app.services.impl;

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
    public User saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UserExistException("Username '" + newUser.getUsername() + "' already exists");
        }
    }

    @Override
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if(user == null)
            return false;

        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteUser(String code) {
        User user = userRepository.findByActivationCode(code);
        userRepository.delete(user);
    }

    @Override
    public boolean activationCode(String login) {
        User user = userRepository.findByUsername(login);
        if(user.getActivationCode() == null)
            return true;
        return false;
    }
}
