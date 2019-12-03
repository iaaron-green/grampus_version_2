package com.app.DTO;

import com.app.enums.Mark;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class DTOLikableProfile {

    private Long id;
    private String fullName;
    private String jobTitle;
    private String profilePicture;
    private Boolean isAbleToLike = true;
    private Map<Mark, Object> achieveCount;


    public DTOLikableProfile(Long id, String fullName, String jobTitle, String profilePicture, Boolean isAbleToLikeYn, Map<Mark, Object> achieveCount) {
        this.id = id;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.profilePicture = profilePicture;
    }

    public DTOLikableProfile(Long id, String fullName, String jobTitle, String profilePicture) {
        this.id = id;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.profilePicture = profilePicture;
    }

    public DTOLikableProfile() {
    }
}
