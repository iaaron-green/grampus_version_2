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
    private Long countOfLikes;
    private Long amountOfComent;
    private List<Comment> comment = new ArrayList<>();
    private Long profileId;

    public DTONews() {
    }

    public DTONews(Long id, String title, String content, String picture, String imgProfile, String nameProfile, String date,
                   Long countOfLikes, Long profileId, Long amountOfComent) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imgProfile = imgProfile;
        this.nameProfile = nameProfile;
        this.picture = picture;
        this.date = date;
        this.countOfLikes = countOfLikes;
        this.profileId = profileId;
        this.amountOfComent = amountOfComent;
    }

    public DTONews(Long id, String title, String date, Long profileId, String content, Long amountOfComent) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.profileId = profileId;
        this.content = content;
        this.amountOfComent = amountOfComent;
    }
}
