package com.app.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String picture;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String fullName;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String jobTitle;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Boolean isAbleToLike;

}
