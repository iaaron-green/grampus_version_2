package com.app.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(length = 100)
    private String title;
    @Column(length = 1000)
    private String content;
    private String picture;
    private String date;
    private Long profileID;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "news", cascade = CascadeType.ALL)
    private List<Comment> comment = new ArrayList<>();

    public News() {};

}

