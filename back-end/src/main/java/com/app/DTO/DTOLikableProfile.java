package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DTOLikableProfile {

    private Long id;
    private String fullName;
    private String jobTitle;
    private String profilePicture;
    private Boolean isAbleToLike = true;

    public DTOLikableProfile(Long id, String fullName, String jobTitle, String profilePicture ) {
        this.id = id;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.profilePicture = profilePicture;
    }
}
