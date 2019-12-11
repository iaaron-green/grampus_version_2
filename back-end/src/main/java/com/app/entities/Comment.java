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

    private String fullname;
    private String imgProfile;
    private String text;
    private String comment_date;
    private Long news_id;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
//    @JoinColumn(name = "news_id")
//    private News news;


    public Comment(String fullname, String imgProfile, String text, String comment_date, Long news_id) {
        this.fullname = fullname;
        this.imgProfile = imgProfile;
        this.text = text;
        this.comment_date = comment_date;
        this.news_id = news_id;
    }

    public Comment() {
    }
}
