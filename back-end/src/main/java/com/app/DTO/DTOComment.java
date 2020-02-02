package com.app.DTO;

import com.app.enums.Mark;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@EqualsAndHashCode
public class DTOComment {

    private Mark ratingType;
    private String comments;
    private Calendar created_date;

    public DTOComment(Mark ratingType, String comments, Calendar created_date) {
        this.ratingType = ratingType;
        this.comments = comments;
        this.created_date = created_date;
    }

    public DTOComment() {
    }
}
