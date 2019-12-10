package com.app.controllers;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOLikeDislike;
import com.app.DTO.DTOProfile;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.enums.RatingSortParam;
import com.app.exceptions.CustomException;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import com.app.services.UserService;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getProfileById(@PathVariable Long profileId, Principal principal) throws CustomException {
        return new ResponseEntity<>(profileService.getDTOProfileById(profileId, principal.getName()), HttpStatus.OK);
    }


    @PostMapping("/{profileId}/addRating")
    public ResponseEntity<?> addDislikeToProfile(@Valid @RequestBody DTOLikeDislike dtoLikeDislike,
                                                 BindingResult result, @PathVariable Long profileId, Principal principal) throws CustomException, MessagingException {
        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        return new ResponseEntity<>(ratingService.addRatingType(dtoLikeDislike, profileId, principal), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> updateProfileById(@Valid @RequestBody DTOProfile profile, BindingResult result,
                                               Principal principal) {

        ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
        if (errorMap != null) return errorMap;

        return new ResponseEntity<>(profileService.updateProfile(profile, principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/{profileId}/photo")
    public void uploadPhoto(@RequestParam("file") MultipartFile file, @PathVariable Long profileId, Principal principal) throws CustomException {
        profileService.saveProfilePhoto(file, profileId, principal);
    }

    @GetMapping("/{profileId}/change-subscription")
    public ResponseEntity<?> changeSubscription(@PathVariable Long profileId, Principal principal) throws CustomException {


        return new ResponseEntity<>(profileService.changeSubscription(profileId, principal), HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<DTOLikableProfile> getAllProfiles(@RequestParam(value = "searchParam", defaultValue = "") String searchParam,
                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @RequestParam(value = "sortParam", defaultValue = "") RatingSortParam sortParam,
                                                      @RequestParam(value = "ratingType", defaultValue = "") Mark ratingType,
                                                      Principal principal) throws CustomException {
        return  profileService.getAllProfilesForLike(principal.getName(), searchParam, page, size, sortParam, ratingType);
    }

    @GetMapping("/achieve")
    public ResponseEntity<?> getAllAchieve() throws CustomException {
        List<Rating> profile = ratingService.getAllAchieves();
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/catalogue")
    public Map<Long, Map<Mark, Long>> getAllInfo() throws CustomException {
        return ratingService.addInfoAchievement();
    }

    @GetMapping("/userRating/{markType}")
    public List<DTOLikableProfile> getUserRating(@PathVariable Mark markType) throws CustomException {
        return ratingService.getUserRatingByMarkType(markType);
    }

    @GetMapping(value = "/userJobTitle/{jobTitle}")
    public List<DTOLikableProfile> getUserByJob(@PathVariable String jobTitle,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "2") Integer size) {
        return userService.findAllByJobTitle(jobTitle, page, size);
    }


    @GetMapping("/catalogueDTO")
    public List<DTOLikableProfile> getAllDTOInfo() {
        return ratingService.addDTOInfoAchievement();
    }
}
