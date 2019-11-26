package com.app.services.impl;

import com.app.DTO.DTONewUser;
import com.app.DTO.DTOUserShortInfo;
import com.app.entities.User;
import com.app.exceptions.UserExistException;
import com.app.repository.UserRepository;
import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UserExistException("Username '" + newUser.getUsername() + "' already exists");
        }
    }
    public List<DTOUserShortInfo> findAllByJobTitle(String jobTitle) {
        List<DTOUserShortInfo> dtoUser = new ArrayList<>();
        Set<User> userData = userRepository.findAllUsersByJobTitle(jobTitle);
        userData.forEach(user -> {
            DTOUserShortInfo s = DTOUserShortInfo.builder()
                    .profileId(user.getId())
                    .jobTitle(user.getJobTitle())
                    .fullName(user.getUsername())
                    .picture(user.getProfile().getProfilePicture())
                    .build();
           dtoUser.add(s);
        });
        return dtoUser;
    }

}


