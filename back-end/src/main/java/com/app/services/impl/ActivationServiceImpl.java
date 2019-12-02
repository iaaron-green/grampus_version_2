package com.app.services.impl;

import com.app.DTO.DTONewUser;
import com.app.entities.ActivationCode;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ActivationRepository;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import com.app.services.UserService;
import com.app.util.CustomException;
import com.app.util.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class ActivationServiceImpl implements ActivationService {

    private UserRepository userRepository;
    private ActivationRepository activationRepository;
    private ProfileRepository profileRepository;
    private JavaMailSender emailSender;
    private UserService userService;
    private ActivationService activationService;
    private MessageSource messageSource;

    @Autowired
    public ActivationServiceImpl(UserRepository userRepository, ActivationRepository activationRepository,
                                 ProfileRepository profileRepository, JavaMailSender emailSender,
                                 UserService userService, ActivationService activationService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.activationRepository = activationRepository;
        this.profileRepository = profileRepository;
        this.emailSender = emailSender;
        this.userService = userService;
        this.activationService = activationService;
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
    public boolean isUserActivate(String login) throws CustomException {
        User user = userRepository.findByUsername(login);

        if (user != null && activationRepository.findByUserId(user.getId()).isActivate()) {
            User newUser = userRepository.findByUsername(user.getUsername());
            Profile newProfile = profileRepository.save(new Profile(newUser));
            return true;
        }
        else
            throw new CustomException(messageSource.getMessage("user.not.exist", null, LocaleContextHolder.getLocale()), Errors.USER_NOT_EXIST);
    }

    @Override
    public String generateCode(Long id) {

        activationRepository.save(new ActivationCode(id));

        String htmlMsg = "<h3>Grampus</h3>"
                + "<img src='https://i.ibb.co/yNsKQ53/image.png'>" +
                "<p>You're profile is register! Thank you.<p>" +
                "To activate you're profile visit next link: http://localhost:8081/api/users/activate/" + id;
        return htmlMsg;
    }

    @Override
    public DTONewUser sendMail(User user) throws CustomException, MessagingException {

        DTONewUser newUser = userService.saveUser(user);

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "utf-8");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        message.setContent(activationService.generateCode(newUser.getUserId()), "text/html");

        helper.setTo(user.getUsername());

        helper.setSubject("Profile registration(GRAMPUS)");

        this.emailSender.send(message);

        return newUser;
    }
}
