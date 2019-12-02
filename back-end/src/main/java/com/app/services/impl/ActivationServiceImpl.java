package com.app.services.impl;

import com.app.DTO.DTONewUser;
import com.app.entities.ActivationCode;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ActivationRepository;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class ActivationServiceImpl implements ActivationService {

    private UserRepository userRepository;
    private ActivationRepository activationRepository;
    private ProfileRepository profileRepository;
    private JavaMailSender emailSender;
    private UserService userService;
    private MessageSource messageSource;

    @Autowired
    public ActivationServiceImpl(UserRepository userRepository, ActivationRepository activationRepository,
                                 ProfileRepository profileRepository, JavaMailSender emailSender,
                                 UserService userService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.activationRepository = activationRepository;
        this.profileRepository = profileRepository;
        this.emailSender = emailSender;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @Override
    public void activateUser(Long id) throws CustomException {
        ActivationCode activationCode = activationRepository.findByUserId(id);
        if (activationCode != null && !activationCode.isActivate()) {
            activationCode.setActivate(true);
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
            User newUser = userRepository.findByEmail(user.getUsername());
            Profile newProfile = profileRepository.save(new Profile(newUser));
            return true;
        }
        else {
            assert user != null;
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(message, true, "utf-8");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            message.setContent(generateHtml(user.getId()), "text/html");
            helper.setTo(user.getEmail());
            helper.setSubject("Profile registration(GRAMPUS)");
            this.emailSender.send(message);

            throw new CustomException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()), Errors.USER_NOT_EXIST);
        }
    }

    @Override
    public String generateHtml(Long id) {
        return "<h3>Grampus</h3>"
                + "<img src='https://i.ibb.co/yNsKQ53/image.png'>" +
                "<p>You're profile is register! Thank you.<p>" +
                "To activate you're profile visit next link: http://localhost:8081/api/users/activate/" + id;
    }

    @Override
    public DTONewUser sendMail(DTONewUser user) throws CustomException, MessagingException {

        DTONewUser newUser = userService.saveUser(user);

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "utf-8");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        activationRepository.save(new ActivationCode(user.getUserId(), String.valueOf(UUID.randomUUID())));

        message.setContent(generateHtml(newUser.getUserId()), "text/html");

        helper.setTo(user.getEmail());

        helper.setSubject("Profile registration(GRAMPUS)");

        this.emailSender.send(message);

        return newUser;
    }
}
