package com.app.web.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
public class AchievementData {
    Long userId;
    Long countLike;
    String userName;
    private String profilePhoto;

}
