package com.app.services;


import com.app.entities.User;
import com.app.util.CustomException;

public interface UserService {

    User saveUser(User newUSer) throws CustomException;
}
