package com.app.config.JWTconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private MessageSource messageSource;


    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, messageSource.getMessage("authorization.error", null, LocaleContextHolder.getLocale()));
//        String jsonLoginResponse = messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale());
//        httpServletResponse.setContentType("application/json");
//        httpServletResponse.setStatus(401);
//        httpServletResponse.getWriter().print(jsonLoginResponse);
    }
}
