package com.app.services;

import com.app.DTO.DTONewUser;
import com.app.exceptions.CustomException;

import javax.mail.MessagingException;


public interface ActivationService {
    void activateUser(Long id) throws CustomException;
    boolean isUserActivate(String login) throws CustomException, MessagingException;
    String generateHtml(Long id);
    DTONewUser sendMail(DTONewUser user) throws  MessagingException, CustomException;
}
