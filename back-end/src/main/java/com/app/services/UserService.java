package com.app.services;


import com.app.DTO.DTONewUser;
import com.app.exceptions.CustomException;

import javax.mail.MessagingException;

public interface UserService {

    DTONewUser saveUser(DTONewUser newUser) throws CustomException, MessagingException;

}
