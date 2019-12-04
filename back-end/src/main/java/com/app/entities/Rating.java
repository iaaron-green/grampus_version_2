package com.app.entities;

import com.app.enums.Mark;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

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

    private String ratingSourceUsername;

    @Enumerated(EnumType.STRING)
    private Mark ratingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profileRating;

    Calendar created_date = Calendar.getInstance();

    private String comment;

}
