package com.app.services;

import com.app.entities.Rating;

import java.util.Map;

public interface RatingService  {

    Rating addLike(Long profileId, Rating updatedRating, String userName);

    Rating addDislike(Long profileId, Rating updatedRating, String userName);

    Map<String, Object> getAndCountLikesByProfileId(Long id);
}
