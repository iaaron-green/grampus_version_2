package com.app.controllers;


import com.app.DTO.DTONewUser;
import com.app.config.JWTconfig.JwtTokenProvider;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.services.ActivationService;
import com.app.services.UserService;
import com.app.validators.JWTLoginSuccessResponse;
import com.app.validators.LoginRequest;
import com.app.validators.UserValidator;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import static com.app.config.Constants.TOKEN_PREFIX;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class AuthorizationController {

   private ValidationErrorService validationErrorService;
   private UserValidator userValidator;
   private ActivationService activationService;
   private UserService userService;

   @Autowired
   public AuthorizationController(ValidationErrorService validationErrorService,
                                  ActivationService activationService,
                                  UserValidator userValidator, UserService userService) {
      this.validationErrorService = validationErrorService;
      this.activationService = activationService;
      this.userValidator = userValidator;

      this.userService = userService;
   }

   @PostMapping("/login")
   public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) throws CustomException, MessagingException {
      ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
      if(errorMap != null)
         return errorMap;

      return ResponseEntity.ok(new JWTLoginSuccessResponse(true, activationService.isUserActivate(loginRequest)));
   }

   @PostMapping("/register")
   public ResponseEntity<?> registerUser(@Valid @RequestBody DTONewUser user, BindingResult result) throws MessagingException, CustomException {
      userValidator.validate(user,result);

      ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
      if(errorMap != null) return errorMap;
      return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
   }

   @GetMapping("/activate/{id}")
   public String activate(@PathVariable Long id) throws CustomException {
      activationService.activateUser(id);
      return "<img style='width:100%' 'height:100%' 'text-align: center' src='https://cdn1.savepice.ru/uploads/2019/11/21/bcadc0172fce5e6a398bb4edcdf8bf7a-full.jpg'>";
   }
}
