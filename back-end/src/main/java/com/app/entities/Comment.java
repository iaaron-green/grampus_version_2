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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String imgProfile;
    private String text;
    private String commentDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "news_id")
    private News news;


    public Comment(String fullName, String imgProfile, String text, String commentDate, News news) {
        this.fullName = fullName;
        this.imgProfile = imgProfile;
        this.text = text;
        this.commentDate = commentDate;
        this.news = news;
    }

    public Comment() {
    }
}
