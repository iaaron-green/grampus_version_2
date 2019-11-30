package com.app.DTO;

import com.app.enums.Mark;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOLikeDislike {

    private Mark ratingType;
    private String comments;

}
