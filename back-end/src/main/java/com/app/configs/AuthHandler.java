package com.app.configs;

import com.app.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class AuthHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken)authentication;
        OAuth2User user = token.getPrincipal();
        Map<String, Object> attributes = user.getAttributes();

        User newuser = new User();
        String username = (String) attributes.get("email");
        String userpass = (String) attributes.get("password");
        newuser.setUsername(username);
        newuser.setPassword(userpass);
        System.out.println(newuser.getUsername());

        httpServletResponse.sendRedirect("/account");
    }
}
