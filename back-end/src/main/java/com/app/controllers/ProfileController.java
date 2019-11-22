package com.app.controllers;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import com.app.util.CustomException;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class ProfileController {

    private ProfileService profileService;
    private ValidationErrorService validationErrorService;
    private RatingService ratingService;

    @Autowired
    public ProfileController(ProfileService profileService, ValidationErrorService validationErrorService, RatingService ratingService) {
        this.profileService = profileService;
        this.validationErrorService = validationErrorService;
        this.ratingService = ratingService;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfileById(@PathVariable Long profileId) {
        Profile profile = null;
        try {
            profile = profileService.getProfileById(profileId);
        } catch (CustomException e) {
            e.getMessage();
        }
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PostMapping("/{profileId}/like")
    public ResponseEntity<?> addLikeToProfile(@Valid @RequestBody Rating rating,
                                              BindingResult result, @PathVariable Long profileId, Principal principal) {
        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        Rating addRating = ratingService.addLike(profileId, rating, principal.getName());

        return new ResponseEntity<>(addRating, HttpStatus.CREATED);
    }

    @PostMapping("/{profileId}/dislike")
    public ResponseEntity<?> addDislikeToProfile(@Valid @RequestBody Rating rating,
                                              BindingResult result, @PathVariable Long profileId, Principal principal) {
        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        Rating addRating = ratingService.addDislike(profileId, rating, principal.getName());

        return new ResponseEntity<>(addRating, HttpStatus.CREATED);
    }

    @PostMapping("")
    public ResponseEntity<?> updateProfileById(@Valid @RequestBody Profile profileEntity, BindingResult result,
                                               Principal principal) {

        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        Profile updatedProfile = profileService.updateProfile(profileEntity, principal.getName());

        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

   @PostMapping("/photo")
   public void uploadPhoto (@RequestParam("file") MultipartFile file, @RequestParam Long id) throws IOException {
       try {
           profileService.saveProfilePhoto(file, id);
      } catch (CustomException e) {
         e.getMessage();
      }
   }

    @GetMapping("/all")
    public Iterable<DTOLikableProfile> getAllProfiles(@RequestParam(value = "fullName", defaultValue = "") String fullName, Principal principal) {

        return fullName.length() > 0 ? profileService.getAllProfilesForLike(principal.getName()).stream()
                .filter(DTOLikableProfile ->
                        Pattern.compile(fullName.toLowerCase()).matcher(DTOLikableProfile.getFullName().toLowerCase()).find()).collect(Collectors.toList()) :
                profileService.getAllProfilesForLike(principal.getName());
    }

//    @GetMapping("/test")
//    public String getTestById()  {
//        return ratingService.addAchievement(43L);
//    }
}
