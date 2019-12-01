package com.app.entities;

import com.app.enums.Mark;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Long userId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "rating_type")
    private Mark ratingType;

    @Column(name = "rating_count")
    private Long ratingCount;

    public Achievement(Long userId) {
        this.userId = userId;
        this.ratingCount = 0L;
    }

    public Achievement() {
    }
}
