package com.app.services.impl;

import com.app.DTO.DTONewUser;
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


    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

//    @Override
//    public DTONewUser saveUser(User newUser) {
//        try {
//            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
//            newUser.setUsername(newUser.getUsername());
//            User userFromDB = userRepository.save(newUser);
//            DTONewUser dtoNewUser = new DTONewUser();
//            dtoNewUser.setUserId(userFromDB.getId());
//            dtoNewUser.setEmail(userFromDB.getUsername());
//
//            return dtoNewUser;
//        } catch (Exception e) {
//            throw new UserExistException("Username '" + newUser.getUsername() + "' already exists");
//        }
//    }

    @Override
    public DTONewUser saveUser(User newUser) throws CustomException {

        if (userRepository.findByUsername(newUser.getUsername()) != null) {
            throw new CustomException(messageSource.getMessage("user.already.exist", null, LocaleContextHolder.getLocale()), Errors.USER_ALREADY_EXIST);
        } else {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            newUser = userRepository.save(newUser);
            DTONewUser dtoNewUser = new DTONewUser();
            dtoNewUser.setUserId(newUser.getId());
            dtoNewUser.setEmail(newUser.getUsername());
            profileService.saveProfile(new Profile(newUser));
            return dtoNewUser;
        }

    }
}
