package com.app.repository;

import com.app.entities.Achievement;
import com.app.enums.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Achievement getAchievementByUserIdAndRatingType(Long userId, Mark ratingType);

}
