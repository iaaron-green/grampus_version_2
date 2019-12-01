package com.app.services.impl;

import com.app.entities.Achievement;
import com.app.enums.Mark;
import com.app.repository.AchievementRepository;
import com.app.repository.RatingRepository;
import com.app.services.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AchievementServiceImpl implements AchievementService {

    private RatingRepository ratingRepository;
    private AchievementRepository achievementRepository;

    @Autowired
    public AchievementServiceImpl(RatingRepository ratingRepository, AchievementRepository achievementRepository) {
        this.ratingRepository = ratingRepository;
        this.achievementRepository = achievementRepository;
    }

    public void recountUserAchievements(Long userId, Mark rating_type){
        Long countAchievFromDB = ratingRepository.countRatingType(userId, rating_type.toString());

        if (countAchievFromDB != 0 && countAchievFromDB % 5 == 0) {
            Long recountAchiev = countAchievFromDB / 5;
            Achievement userAchievement = achievementRepository.getAchievementByUserIdAndRatingType(userId, rating_type);
            if (userAchievement == null) {
                userAchievement = new Achievement(userId);
                userAchievement.setRatingType(rating_type);
            }

            if (recountAchiev > userAchievement.getRatingCount()) {
                userAchievement.setRatingCount(recountAchiev);
                achievementRepository.save(userAchievement);
            }
        }
    }
}
