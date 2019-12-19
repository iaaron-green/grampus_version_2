package com.app.services;

import com.app.exceptions.CustomException;
import com.app.validators.LoginRequest;

import javax.mail.MessagingException;


public interface ActivationService {
    void activateUser(Long id) throws CustomException;
    String isUserActivate(LoginRequest loginRequest) throws CustomException, MessagingException;
    void sendMail(String userEmail, String subject, String article, String messageText) throws MessagingException;
}
