package com.app.services;

import org.springframework.stereotype.Service;


public interface ActivationService {
    void activateUser(Long id);
    boolean isUserActivate(String login);
    String generateCode(Long id);

}
