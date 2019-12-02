package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
public class DTONewUser {

    private Long userId;
    @NotBlank()
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "FullName is required")
    private String fullName;
}
