package com.app.entities;

import com.app.entities.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "achieves")
public class Achieve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ratingSourceUsername;

    private String ratingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achieve_id")
    @JsonIgnore
    private Profile profileAchieve;

    public Achieve(Profile profileAchieve) {
        this.profileAchieve = profileAchieve;
    }

    public Achieve() {
    }
}