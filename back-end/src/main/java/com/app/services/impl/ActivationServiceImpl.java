package com.app.services.impl;

import com.app.configtoken.Constants;
import com.app.entities.ActivationCode;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ActivationRepository;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class ActivationServiceImpl implements ActivationService {

    private UserRepository userRepository;
    private ActivationRepository activationRepository;
    private ProfileRepository profileRepository;
    private JavaMailSender emailSender;
    private MessageSource messageSource;

    @Autowired
    public ActivationServiceImpl(UserRepository userRepository, ActivationRepository activationRepository,
                                 ProfileRepository profileRepository, JavaMailSender emailSender,
                                 MessageSource messageSource) {
        this.userRepository = userRepository;
        this.activationRepository = activationRepository;
        this.profileRepository = profileRepository;
        this.emailSender = emailSender;
        this.messageSource = messageSource;
    }

    @Override
    public void activateUser(Long id) throws CustomException {
        ActivationCode activationCode = activationRepository.findByUserId(id);
        if (activationCode != null && !activationCode.isActivate()) {
            activationCode.setActivate(true);
            activationCode.setDate(new Date(System.currentTimeMillis()));
            activationRepository.save(activationCode);
            User user = userRepository.getById(id);
            profileRepository.save(new Profile(user));
        }
        else
            throw new CustomException(messageSource.getMessage("activation.code.is.active", null, LocaleContextHolder.getLocale()), Errors.ACTIVATION_CODE_IS_ACTIVE);
    }

    @Override
    public boolean isUserActivate(String login) throws CustomException, MessagingException {
        User user = userRepository.findByEmail(login);

        if (user != null && activationRepository.findByUserId(user.getId()).isActivate()) {
            return true;
        }
        else {
            if (user == null) throw new CustomException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()), Errors.USER_NOT_EXIST);

            sendMail(user.getEmail(), Constants.REG_MAIL_SUBJECT, Constants.REG_MAIL_ARTICLE, Constants.REG_MAIL_MESSAGE + user.getId());
            throw new CustomException(messageSource.getMessage("user.not.activated", null, LocaleContextHolder.getLocale()), Errors.USER_NOT_ACTIVATED);
        }
    }

    public void sendMail(String userEmail, String subject, String article, String messageText) throws  MessagingException {

        String content = "<h3>" + article + "</h3>" +
                "<p style='margin-bottom:15px'>" + messageText + "</p>" +
                "<img src='https://i.ibb.co/yNsKQ53/image.png'>";

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "utf-8");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        message.setContent(content, "text/html");

        helper.setTo(userEmail);

        helper.setSubject(subject);

        emailSender.send(message);
    }
}
