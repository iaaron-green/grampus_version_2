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
@Builder
public class DTONews {
    private Long id;
    private String title;
    private String content;
    private String imgProfile;
    private String nameProfile;
    private String picture;
    private String date;
    private Long countOfLikes;
    private List<Comment> comment = new ArrayList<>();

    public DTONews() {
    }

    public DTONews(Long id, String title, String content, String picture, String imgProfile, String nameProfile, String date,
                   Long countOfLikes, List<Comment> comment) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imgProfile = imgProfile;
        this.nameProfile = nameProfile;
        this.picture = picture;
        this.date = date;
        this.countOfLikes = countOfLikes;
        this.comment = comment;
    }
}
