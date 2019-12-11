package com.app.validators;


import com.app.DTO.DTONewUser;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return  com.app.DTO.DTONewUser.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        DTONewUser user = (DTONewUser) object;
        if (user.getPassword().length() < 6){
            errors.rejectValue("password","Length", "Password must be at least 6 characters");
        }
    }
}
