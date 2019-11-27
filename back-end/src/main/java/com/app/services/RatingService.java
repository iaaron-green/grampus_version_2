package com.app.services;

import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.DTO.DTOAchievement;

import java.util.List;
import java.util.Map;

public interface RatingService {

    Rating addLike(Long profileId, Rating updatedRating, String userName);

    List<Rating> getAllAchieves();

    Map<Long, Map<String, Long>> addInfoAchievement();

    List<DTOAchievement> getUserRatingByType(Mark markType);

    Rating addDislike(Long profileId, Rating updatedRating, String userName);

    Map<String, Object> getAndCountLikesByProfileId(Long id);
}
