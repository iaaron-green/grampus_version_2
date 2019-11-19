package com.app.controllers;


import com.app.configmail.MyConstants;
import com.app.configtoken.JwtTokenProvider;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.services.ProfileService;
import com.app.services.UserService;
import com.app.validators.JWTLoginSuccessResponse;
import com.app.validators.LoginRequest;
import com.app.validators.UserValidator;
import com.app.validators.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import java.util.UUID;

import static com.app.configtoken.Constants.TOKEN_PREFIX;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class AuthorizationController {

   private ValidationErrorService validationErrorService;
   private UserService userService;
   private UserValidator userValidator;
   private JwtTokenProvider tokenProvider;
   private AuthenticationManager authenticationManager;
   private ProfileService profileService;

   @Autowired
   public JavaMailSender emailSender;

   @Autowired
   public AuthorizationController(ValidationErrorService validationErrorService, UserService userService,
                         UserValidator userValidator,
                         JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager, ProfileService profileService) {
      this.validationErrorService = validationErrorService;
      this.userService = userService;
      this.userValidator = userValidator;
      this.tokenProvider = tokenProvider;
      this.authenticationManager = authenticationManager;
      this.profileService = profileService;
   }

   @PostMapping("/login")
   public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
      ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
      if(errorMap != null) return errorMap;

      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = TOKEN_PREFIX +  tokenProvider.provideToken(authentication);

      return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
   }

   @PostMapping("/register")
   public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) throws MessagingException {
      userValidator.validate(user,result);

      ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
      if(errorMap != null)return errorMap;

      User newUser = userService.saveUser(user);
      newUser.setActivationCode(UUID.randomUUID().toString());
      Profile newProfile = profileService.saveProfile(new Profile(newUser));
      MimeMessage message = emailSender.createMimeMessage();

      boolean multipart = true;

      MimeMessageHelper helper = null;
      try {
         helper = new MimeMessageHelper(message, multipart, "utf-8");
      } catch (MessagingException e) {
         e.printStackTrace();
      }

      String htmlMsg = "<h3>Grampus</h3>"
              +"<img src='https://i.ibb.co/yNsKQ53/image.png'>" +
              "<p>You're profile is register! Thank you.<p>" +
               "To activate you're profile input activate code: "+ newUser.getActivationCode();
      //com.app.img src='https://www.earticleblog.com/wp-content/uploads/2017/03/sucess-home-tester.png'
      message.setContent(htmlMsg, "text/html");

      helper.setTo(user.getUsername());

      helper.setSubject("Profile registration(GRAMPUS)");
      helper.setText("You're profile is register! Thank you.");

      this.emailSender.send(message);

      return  new ResponseEntity<>(newUser, HttpStatus.CREATED);
   }


}
