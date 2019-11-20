package com.app.controllers;

import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ActivationController {

    @Autowired
    private UserService userService;

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated)
            return "Try";
//            return "<h3>Success</h3>"
//                    +"<img src='https://ibb.co/xsN9WM3'>" +
//                    "<p>Have been successfully activation Grampus<p>";

        return "LOCKED";
    }
}
