package com.app.DTO;

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
