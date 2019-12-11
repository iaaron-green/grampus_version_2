package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOComment {
    private Long id;
    private String picture;
    private String date;
    private String text;

    public DTOComment() {
    }

    public DTOComment(Long id, String picture, String date, String text) {
        this.id = id;
        this.picture = picture;
        this.date = date;
        this.text = text;
    }
}
