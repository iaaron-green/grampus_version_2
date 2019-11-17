package com.app.controllers;

import com.app.services.ProfileService;
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


}
