package com.app.controllers;

import com.app.services.ProfileService;
import com.app.util.CustomException;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

   @PostMapping("/photo")
   public String uploadPhoto (@RequestParam("file") MultipartFile file, @RequestParam Long id) throws IOException {
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

      String pictureURL = null;
      try {
        pictureURL = profileService.saveProfilePhoto(file, id).getProfilePicture();
      } catch (CustomException e) {
         e.getMessage();
      }
      return pictureURL;
   }
}
