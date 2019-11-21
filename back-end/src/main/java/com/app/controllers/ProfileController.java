package com.app.controllers;

import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.services.RatingService;
import com.app.services.impl.ProfileIdentifierException;
import com.app.services.impl.ProfileServiceImpl;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class ProfileController {

    private ProfileServiceImpl profileService;
    private ValidationErrorService validationErrorService;
    private RatingService ratingService;

    @Autowired
    public ProfileController(ProfileServiceImpl profileService, ValidationErrorService validationErrorService, RatingService ratingService) {
        this.profileService = profileService;
        this.validationErrorService = validationErrorService;
        this.ratingService = ratingService;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfileById(@PathVariable Long profileId) throws ProfileIdentifierException {
        Optional<Profile> profile = profileService.getProfileById(profileId);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> updateProfileById(@Valid @RequestBody Profile profileEntity, BindingResult result,
                                               Principal principal) {

        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        Profile updatedProfile = profileService.updateProfile(profileEntity, principal.getName());

        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @GetMapping("/achievement")
    public ResponseEntity<?> getAchieveById()  {
        List<Rating> profile = profileService.getAchives();
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/test")
    public String getTestById()  {
        return ratingService.addAchievement(43L);
    }
}
