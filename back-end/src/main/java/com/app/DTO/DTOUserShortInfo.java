package com.app.DTO;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class DTOUserShortInfo {

    Long profileId;
    String picture;
    String fullName;
    String jobTitle;
    Boolean isAbleToLike;

}
