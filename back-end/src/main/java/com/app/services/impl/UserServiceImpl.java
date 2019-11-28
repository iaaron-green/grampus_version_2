package com.app.services.impl;

import com.app.DTO.DTONewUser;
import com.app.DTO.DTOUserShortInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public DTONewUser saveUser(DTONewUser newUser) throws CustomException {

        LOGGER.info("Check if user already exist");
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
            profileService.saveProfile(new Profile(userFromDB));
            LOGGER.info("New user registration successful");
            newUser.setPassword("******");
            return newUser;
        }
    }

    public List<DTOUserShortInfo> findAllByJobTitle(String jobTitle) {
        List<DTOUserShortInfo> dtoUser = new ArrayList<>();
        Set<User> userData = userRepository.findAllUsersByJobTitle(jobTitle);
        userData.forEach(user -> {
            DTOUserShortInfo s = DTOUserShortInfo.builder()
                    .profileId(user.getId())
                    .jobTitle(user.getJobTitle())
                    .fullName(user.getEmail())
                    .picture(user.getProfile().getProfilePicture())
                    .build();
           dtoUser.add(s);
        });
        return dtoUser;
    }

}


