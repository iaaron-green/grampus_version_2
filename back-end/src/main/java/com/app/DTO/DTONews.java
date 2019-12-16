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
    private Long profileId;
    private Long countOfComments;
    public DTONews() {
    }

    public DTONews(Long id, String title, String date, Long profileId, String content, Long countOfComments,
                   String picture, String imgProfile, String nameProfile) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.profileId = profileId;
        this.content = content;
        this.countOfComments = countOfComments;
        this.picture = picture;
        this.imgProfile = imgProfile;
        this.nameProfile = nameProfile;
    }
}
