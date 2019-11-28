package com.app.services;

import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.util.CustomException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;


public interface ActivationService {
    void activateUser(Long id) throws CustomException;
    boolean isUserActivate(String login) throws CustomException;
    String generateCode(Long id);
    DTONewUser sendMail(User user) throws CustomException, MessagingException;
}
