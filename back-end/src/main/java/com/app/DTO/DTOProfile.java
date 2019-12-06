package com.app.DTO;

import com.app.enums.Mark;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class DTOProfile {

    private Long id;
    private String profilePicture;
    private Long likes;
    private Long dislikes;
    private String skype;
    private String phone;
    private String telegram;
    private String skills;
    private String email;
    private String jobTitle;
    private String country;
    private String fullName;
    private Boolean isAbleToLike = false;
    private Boolean isFollowing = false;
    private Map<Mark, Object> likesNumber;
    private List<String> comments;
}
