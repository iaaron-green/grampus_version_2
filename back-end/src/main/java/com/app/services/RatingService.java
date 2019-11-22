package com.app.services;

import com.app.entities.Rating;

public interface RatingService  {

    Rating addLike(Long profileId, Rating updatedRating, String userName);

    String addAchievement(Long id);

}
