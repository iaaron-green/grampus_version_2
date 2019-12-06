package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository userRepository;
    private ProfileService profileService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ProfileService profileService, BCryptPasswordEncoder bCryptPasswordEncoder, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.profileService = profileService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.messageSource = messageSource;
    }

    @Override
    public DTONewUser saveUser(DTONewUser newUser) throws CustomException {

        if (userRepository.findByEmail(newUser.getEmail()) != null) {
            throw new CustomException(messageSource.getMessage("user.already.exist", null, LocaleContextHolder.getLocale()), Errors.USER_ALREADY_EXIST);
        } else {
            User userFromDB = new User();
            userFromDB.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            userFromDB.setEmail(newUser.getEmail());
            userFromDB.setFullName(newUser.getFullName());
            userFromDB = userRepository.save(userFromDB);
            newUser.setUserId(userFromDB.getId());
            newUser.setEmail(userFromDB.getEmail());
            LOGGER.info("New user registration successful");
            newUser.setPassword("******");
            return newUser;
        }
    }

    @Override
    public List<DTOLikableProfile> findAllByJobTitle(String jobTitle, int page, int size) {
        List<DTOLikableProfile> dtoUser = new ArrayList<>();
        Page<User> userData = userRepository.findAllUsersByJobTitle(jobTitle, pageRequest(page, size));
        userData.forEach(user -> {
            DTOLikableProfile s = DTOLikableProfile.builder()
                    .id(user.getId())
                    .jobTitle(user.getJobTitle())
                    .fullName(user.getEmail())
                    .profilePicture(user.getProfile().getProfilePicture())
                    .build();
           dtoUser.add(s);
        });
        return dtoUser;
    }

    private Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }
}


