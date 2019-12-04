package com.app.services;

import com.app.exceptions.CustomException;

import javax.mail.MessagingException;


public interface ActivationService {
    void activateUser(Long id) throws CustomException;
    boolean isUserActivate(String login) throws CustomException, MessagingException;
    void sendMail(String userEmail, String subject, String article, String messageText) throws MessagingException;
}
