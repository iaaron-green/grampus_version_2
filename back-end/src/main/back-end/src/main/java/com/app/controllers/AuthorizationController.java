package com.app.controllers;


import com.app.configtoken.JwtTokenProvider;
import com.app.entities.User;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import com.app.services.UserService;
import com.app.validators.JWTLoginSuccessResponse;
import com.app.validators.LoginRequest;
import com.app.validators.UserValidator;
import com.app.validators.ValidationErrorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import static com.app.configtoken.Constants.TOKEN_PREFIX;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class AuthorizationController {

   private static final Logger logger = Logger.getLogger(AuthorizationController.class);
   private ValidationErrorService validationErrorService;
   private UserService userService;
   private UserValidator userValidator;
   private JwtTokenProvider tokenProvider;
   private AuthenticationManager authenticationManager;
   @Autowired
   private ActivationService activationService;

   @Autowired
   private JavaMailSender emailSender;

   @Autowired
   private UserRepository userRepository;


   @Autowired
   public AuthorizationController(ValidationErrorService validationErrorService, UserService userService,
                         UserValidator userValidator,
                         JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager) {
      this.validationErrorService = validationErrorService;
      this.userService = userService;
      this.userValidator = userValidator;
      this.tokenProvider = tokenProvider;
      this.authenticationManager = authenticationManager;
   }

   @PostMapping("/login")
   public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
      logger.info("|login| - is start");
      ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
      if(errorMap != null)
         return errorMap;

      String userName = loginRequest.getUsername();
      if (!activationService.isUserActivate(userName)) {
         logger.info("|login| - user is not activate");
         return new ResponseEntity<>(HttpStatus.LOCKED);
      }

      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = TOKEN_PREFIX +  tokenProvider.provideToken(authentication);

      logger.info("|login| - success");
      return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
   }

   @PostMapping("/register")
   public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) throws MessagingException {
      logger.info("|register| - is start");
      userValidator.validate(user,result);

      ResponseEntity<?> errorMap = validationErrorService.mapValidationService(result);
      if(errorMap != null) return errorMap;

      if(userRepository.findByUsername(user.getUsername()) != null)
      {
         logger.info("|register| - can't find user");
         return new ResponseEntity<>(HttpStatus.LOCKED);
      }
      User newUser = userService.saveUser(user);
      logger.info("|register| - is start");
      MimeMessage message = emailSender.createMimeMessage();

      boolean multipart = true;

      MimeMessageHelper helper = null;
      try {
         helper = new MimeMessageHelper(message, multipart, "utf-8");
      } catch (MessagingException e) {
         e.printStackTrace();
      }



      message.setContent(activationService.generateCode(newUser.getId()), "text/html");

      helper.setTo(user.getUsername());

      helper.setSubject("Profile registration(GRAMPUS)");

      this.emailSender.send(message);
      logger.info("|register| - email was sent");
      logger.info("|register| - success");
      return new ResponseEntity<>(newUser, HttpStatus.CREATED);
   }

   @GetMapping("/activate/{id}")
   public String activate(@PathVariable Long id) {
      logger.info("|activate| - user click on url");
      activationService.activateUser(id);
      logger.info("|activate| - user was activated");
      return "<img style='width:100%' 'height:100%' 'text-align: center' src='https://cdn1.savepice.ru/uploads/2019/11/21/bcadc0172fce5e6a398bb4edcdf8bf7a-full.jpg'>";
   }
}
