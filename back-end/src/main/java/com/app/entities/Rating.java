package com.app.entities;

import com.app.enums.Mark;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "ratings")
public class Rating {
    public Rating() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

/*    @Ignore
    private String ratingSourceUsername;*/

    @Column(name = "user_id")
    private int profile_id;

    private String ratingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profileRating;

//    public Rating() {
//        this.profileRating = profileRating;
//    }
}
