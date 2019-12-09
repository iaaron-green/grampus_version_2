package com.app.schedulers;

import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Set;

@Component
public class SchedulerPlanningImpl implements ShedulerPlanning {

    private UserRepository userRepository;
    private ActivationService activationService;
    public static int DATE_SHIFT = 0;

    @Autowired
    public SchedulerPlanningImpl(UserRepository userRepository, ActivationService activationService) {
        this.userRepository = userRepository;
        this.activationService = activationService;
    }

    @Scheduled(cron = "${cronExpression}")
    public void checkNotActivatedUsers() {
        Set<String> sendMailUsers = findNotActivatedByRegistrationDate();
        sendMailUsers.forEach(u -> {
            try {
                activationService.sendMail(u, "subject", "arrticle", "message" );
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    //method for selecting users by registration date and check NOT ACTIVATED
    @Override
    public Set<String> findNotActivatedByRegistrationDate() {
        Set<String> allCheckedRegistrationDate = userRepository.getByRegistrationDate(DATE_SHIFT);
        return allCheckedRegistrationDate;
    }
}