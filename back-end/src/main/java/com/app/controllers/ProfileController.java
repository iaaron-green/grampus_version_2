package com.app.controllers;

import com.app.entities.Profile;
import com.app.services.ProfileService;
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

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin
public class ProfileController {

   private ProfileService profileService;
   private ValidationErrorService validationErrorService;

   @Autowired
   public ProfileController(ProfileService profileService, ValidationErrorService validationErrorService) {
      this.profileService = profileService;
      this.validationErrorService = validationErrorService;
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
}
