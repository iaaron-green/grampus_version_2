package com.app.services;

import com.app.entities.Rating;

public interface RatingService  {

    Rating addLike(Long profileId, Rating updatedRating, String userName);

    String getAndCountLikesByProfileId(Long id);

    String addAchievement(Long id);

}
