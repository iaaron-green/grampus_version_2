package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTONewsComment {
    private Long id;
    private String picture;
    private String fullname;
    private String date;
    private String text;

    public DTONewsComment() {
    }

    public DTONewsComment(Long id, String picture, String date, String text, String fullname) {
        this.id = id;
        this.picture = picture;
        this.date = date;
        this.text = text;
        this.fullname = fullname;
    }
}
