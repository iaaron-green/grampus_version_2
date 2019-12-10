package com.app.controllers;

import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import com.app.services.UserService;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
@Component
@Profile("test")
public class TestController {
    private ValidationErrorService validationErrorService;
    private RatingService ratingService;
    private UserService userService;
    private ProfileService profileService;
    private UserRepository userRepository;


    @Autowired
    public TestController(ValidationErrorService validationErrorService, RatingService ratingService, UserService userService, ProfileService profileService, UserRepository userRepository) {
        this.validationErrorService = validationErrorService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfileById(@PathVariable Long profileId) throws CustomException {

        User loggedUser = userRepository.getById(2L);
        return new ResponseEntity<>(profileService.getDTOProfileById(profileId, loggedUser.getEmail()), HttpStatus.OK);
    }

}
