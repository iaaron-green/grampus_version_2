package com.app.controllers;

import com.app.services.ProfileService;
import com.app.util.CustomException;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

      String pictureURL = null;
      try {
        pictureURL = profileService.saveProfilePhoto(file, id).getProfilePicture();
      } catch (CustomException e) {
         e.getMessage();
      }
      return pictureURL;
   }
}
