package com.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "comment")
@Builder
public class Comment {
    @Id
    private Long id;

    private String fullname;
    private String imgProfile;
    private String text;
    private String comment_date;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "news_id")
    private News news;

    public Comment(String fullname, String imgProfile, String text, String comment_date) {
        this.fullname = fullname;
        this.imgProfile = imgProfile;
        this.text = text;
        this.comment_date = comment_date;
    }


    public Comment() {
    }
}
