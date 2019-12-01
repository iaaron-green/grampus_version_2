package com.app.services;

import com.app.enums.Mark;

public interface AchievementService {

    void recountUserAchievements(Long userId, Mark rating_type);
}
