package com.app.DTO;

import com.app.enums.Mark;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class DTOProfile {

    private Long id;
    private String profilePicture;
    private Long likes;
    private Long dislikes;
    private String information;
    private String skills;
    private String email;
    private String jobTitle;
    private String fullName;
    private Map<Mark, Object> likesNumber;
    private boolean isAbleToLike = true;

}
