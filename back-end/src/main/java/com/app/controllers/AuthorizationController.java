package com.app.controllers;


import com.app.DTO.DTONewUser;
import com.app.configtoken.Constants;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.UserRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class AuthorizationController {

   private ValidationErrorService validationErrorService;
   private UserValidator userValidator;
   private ActivationService activationService;
   private UserService userService;
   private UserRepository userRepository;
   private MessageSource messageSource;

   @Autowired
   public AuthorizationController(ValidationErrorService validationErrorService,
                                  ActivationService activationService,
                                  UserValidator userValidator, UserService userService,
                                  UserRepository userRepository, MessageSource messageSource) {
      this.validationErrorService = validationErrorService;
      this.activationService = activationService;
      this.userValidator = userValidator;
      this.userService = userService;
      this.userRepository = userRepository;
      this.messageSource = messageSource;
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

   @PostMapping("/changePassword")
   public ResponseEntity<?> changePassword(@RequestParam("email") String email) throws MessagingException, CustomException {
      if(userRepository.findByEmail(email) != null) {
         activationService.sendMail(email, "Reset password", "To reset password click->", Constants.URL_RESET_PASSWORD + email);
         return new ResponseEntity<>(HttpStatus.OK);
      }
      else
         throw new CustomException(messageSource.getMessage("email.not.found", null, LocaleContextHolder.getLocale()), Errors.EMAIL_NOT_FOUNT);
   }

   @GetMapping("/changePassword/{email}")
   public ResponseEntity<?> confirmEmail(@PathVariable("email") String email)
   {
      User user = userRepository.findByEmail(email);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}
