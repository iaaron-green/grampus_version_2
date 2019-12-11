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
    private Boolean isAbleToLike = false;
    private Map<Mark, Object> achieveCount;
    private Long totalLikes;
    private Long totalDisLikes;


    public DTOLikableProfile(Long id, String fullName, String jobTitle, String profilePicture, Long totalLikes) {
        this.id = id;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.profilePicture = profilePicture;
        this.totalLikes = totalLikes;
    }

    public DTOLikableProfile(Long id, String fullName, String jobTitle, String profilePicture, Long totalLikes, Long totalDisLikes) {
        this.id = id;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.profilePicture = profilePicture;
        this.totalLikes = totalLikes;
        this.totalDisLikes = totalDisLikes;
    }


    public DTOLikableProfile(Long id, String fullName, String jobTitle, String profilePicture, Boolean isAbleToLike,  Map<Mark, Object> achieveCount, Long totalLikes, Long totalDisLikes) {
        this.id = id;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.profilePicture = profilePicture;
        this.isAbleToLike = isAbleToLike;
        this.achieveCount = achieveCount;
        this.totalLikes = totalLikes;
        this.totalDisLikes = totalDisLikes;
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
