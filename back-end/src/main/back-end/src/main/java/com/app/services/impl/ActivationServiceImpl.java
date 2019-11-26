package com.app.services.impl;

import com.app.entities.ActivationCode;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ActivationRepository;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationServiceImpl implements ActivationService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivationRepository activationRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Override
    public void activateUser(Long id) {
        ActivationCode activationCode = activationRepository.findByUserId(id);
        if(activationCode != null && !activationCode.isActivate()) {
            activationCode.setActivate(true);
            activationRepository.save(activationCode);
        }

        // trow exception
    }

    @Override
    public boolean isUserActivate(String login) {
        User user = userRepository.findByUsername(login);

        if(user != null &&activationRepository.findByUserId(user.getId()).isActivate()) {
            User newUser = userRepository.findByUsername(user.getUsername());
            Profile newProfile = profileRepository.save(new Profile(newUser));
            return true;
        }
        else
            return false;
            //throw exception
    }

    @Override
    public String generateCode(Long id) {

        activationRepository.save(new ActivationCode(id));

        String htmlMsg = "<h3>Grampus</h3>"
                +"<img src='https://i.ibb.co/yNsKQ53/image.png'>" +
                "<p>You're profile is register! Thank you.<p>" +
                "To activate you're profile visit next link: http://localhost:8081/api/users/activate/" + id;
        return htmlMsg;
    }


}
