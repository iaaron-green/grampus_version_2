package com.app.schedulers;

import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class SchedulerPlanningImpl implements ShedulerPlanning {
    private UserRepository userRepository;
    private ActivationService activationService;
    private int dateShift = 2;
    LocalDate checkedRegistrationDate = LocalDate.now().minusDays(dateShift);

    @Autowired
    public SchedulerPlanningImpl(UserRepository userRepository, ActivationService activationService) {
        this.userRepository = userRepository;
        this.activationService = activationService;
    }

    @Scheduled(cron = "0 11 00 ? * *")
    public void checkNotActivatedUsers() throws MessagingException, CustomException {
        Set<User> sendMailUsers = checkNotActivationUsers();
        activationService.sendMail((DTONewUser) sendMailUsers);
    }

    //method for selecting users by registration date
    @Override
    public Set<User> findByRegistrationDate() {
        Set<User> allCheckedRegistrationDate = new HashSet<>();
        userRepository.findAll().iterator().forEachRemaining(user -> {
            if (asLocalDate(user.getRegistrationDate()) == checkedRegistrationDate) {
                return;
            } else {
                allCheckedRegistrationDate.add(user);
            };
        });
        return allCheckedRegistrationDate;
    }

    private Set<User> checkNotActivationUsers() {
        Set<User> usersByRegistrationDate = findByRegistrationDate();
        Set<User> notActivated = new HashSet<>();
        usersByRegistrationDate.iterator().forEachRemaining(user -> {
            try {
                if (activationService.isUserActivate(user.getEmail())) {
                    return;
                } else {
                    notActivated.add(user);
                }
            } catch (CustomException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        return notActivated;
    }

    private static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}