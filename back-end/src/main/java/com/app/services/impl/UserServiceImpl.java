package com.app.services.impl;

import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.services.UserService;
import com.app.util.CustomException;
import com.app.util.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private MessageSource messageSource;

    @Override
    public User saveUser(User newUser) throws CustomException {

        LOGGER.info("Check if user already exist");
        if (userRepository.findByUsername(newUser.getUsername()) != null) {
            throw new CustomException(messageSource.getMessage("user.already.exist", null, LocaleContextHolder.getLocale()), Errors.USER_ALREADY_EXIST);
        } else {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            newUser = userRepository.save(newUser);
            profileService.saveProfile(new Profile(newUser));
            LOGGER.info("New user registration successful");
            return newUser;
        }
    }
}
