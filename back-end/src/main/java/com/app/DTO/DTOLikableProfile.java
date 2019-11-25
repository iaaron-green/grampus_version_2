package com.app.DTO;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class DTOLikableProfile {

    private Long profileId;
    private String picture;
    private String fullName;
    private String jobTitle;
    private Boolean isAbleToLike;

}
