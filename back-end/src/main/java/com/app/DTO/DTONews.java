package com.app.DTO;

import com.app.entities.Comment;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class DTONews {
    private Long id;
    private String title;
    private String content;
    private String imgProfile;
    private String nameProfile;
    private String picture;
    private String date;
    private List<Comment> comment = new ArrayList<>();
    private Long profileId;

    public DTONews() {
    }

    public DTONews(Long id, String title, String content, String picture, String imgProfile, String nameProfile, String date
            , Long profileId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imgProfile = imgProfile;
        this.nameProfile = nameProfile;
        this.picture = picture;
        this.date = date;
        this.profileId = profileId;
    }

    public DTONews(Long id, String title, String date, Long profileId, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.profileId = profileId;
        this.content = content;
    }
}
