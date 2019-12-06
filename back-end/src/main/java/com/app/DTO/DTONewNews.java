package com.app.DTO;

import com.app.entities.Profile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class DTONewNews {
    private String title;
    private String content;
    private String imgProfile;
    private String nameProfile;
    private String picture;
    private Date date;

    public DTONewNews(String title, String content, String picture, String imgProfile,String nameProfile, Date date) {
        this.title = title;
        this.content = content;
        this.imgProfile = imgProfile;
        this.nameProfile = nameProfile;
        this.picture = picture;
        this.date = date;
    }
}
