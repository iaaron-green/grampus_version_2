package com.app.controllers;

import com.app.entities.User;
import com.app.services.AuthorizationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
public class AuthorizationController {

    @Autowired
    AuthorizationService authorizationService;

    @PostMapping("/register")
    public String registration(@Valid @RequestBody User user,){

        authorizationService.userRegister(user);
        UserRecord userRecord = FirebaseAuth.getInstance();

        System.out.println("Successfully created new user: " + userRecord.getUid());

        return new Gson().toJson(user);
    }
}
