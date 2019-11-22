package com.app.services;


import com.app.entities.User;

public interface UserService {

    User saveUser(User newUSer);

    boolean activateUser(String code);

    void deleteUser(String code);

    boolean activationCode(String login);
}
