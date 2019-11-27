package com.app.controllers;

import com.app.DTO.DTOAchievement;
import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOProfile;
import com.app.DTO.DTOUserShortInfo;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import com.app.services.UserService;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class ProfileController {

    private ProfileService profileService;
    private ValidationErrorService validationErrorService;
    private RatingService ratingService;
    private UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, ValidationErrorService validationErrorService, RatingService ratingService, UserService userService) {
        this.profileService = profileService;
        this.validationErrorService = validationErrorService;
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfileById(@PathVariable Long profileId) throws CustomException {
        return new ResponseEntity<>(profileService.getDTOProfileById(profileId), HttpStatus.OK);
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
    public ResponseEntity<?> updateProfileById(@Valid @RequestBody DTOProfile profile, BindingResult result,
                                               Principal principal) {

        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        return new ResponseEntity<>(profileService.updateProfile(profile, principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/photo")
    public void uploadPhoto(@RequestParam("file") MultipartFile file, @RequestParam Long id) throws IOException {
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

    @GetMapping("/achieve")
    public ResponseEntity<?> getAllAchieve() {
        List<Rating> profile = ratingService.getAllAchieves();
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

//    @GetMapping("/test")
//    public String getTestById() {
//        return ratingService.addAchievement(3L);
//    }

    @GetMapping("/catalogue")
    public Map<Long, Map<String, Long>> getAllInfo() {
        return ratingService.addInfoAchievement();
    }

    @GetMapping("/userRating/{markType}")
    public List<DTOAchievement> getUserRating(@PathVariable Mark markType) {
        return ratingService.getUserRatingByType(markType);
    }

    @GetMapping("/userJobTitle/{jobTitle}")
    public List<DTOUserShortInfo> getUserByJob(@PathVariable String jobTitle) {
        return userService.findAllByJobTitle(jobTitle);
    }
}
