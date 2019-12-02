package com.app.validators;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Username cannot be blank")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;

    public String getUsername() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
