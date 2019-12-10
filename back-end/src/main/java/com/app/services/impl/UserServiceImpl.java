package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTONewUser;
import com.app.config.Constants;
import com.app.entities.ActivationCode;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ActivationRepository;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private MessageSource messageSource;
    private ActivationService activationService;
    private ActivationRepository activationRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MessageSource messageSource,
                           ActivationService activationService, ActivationRepository activationRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.messageSource = messageSource;
        this.activationService = activationService;
        this.activationRepository = activationRepository;
    }

    @Override
    public DTONewUser saveUser(DTONewUser newUser) throws CustomException, MessagingException {

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
            newUser.setPassword("******");

            activationRepository.save(new ActivationCode(newUser.getUserId(), String.valueOf(UUID.randomUUID())));
            activationService.sendMail(newUser.getEmail(), Constants.REG_MAIL_SUBJECT, Constants.REG_MAIL_ARTICLE, Constants.REG_MAIL_MESSAGE + newUser.getUserId());
            return newUser;
        }
    }

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


