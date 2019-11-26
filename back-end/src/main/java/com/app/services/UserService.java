package com.app.services;


import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.util.CustomException;

public interface UserService {

    DTONewUser saveUser(User newUSer) throws CustomException;
}
