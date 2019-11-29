package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;

import java.util.List;
import java.util.Map;

public interface RatingService {

    Rating addLike(Long profileId, Rating updatedRating, String userName) throws CustomException;

    List<Rating> getAllAchieves() throws CustomException;

    Map<Long, Map<String, Long>> addInfoAchievement() throws CustomException;

    List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) throws CustomException;

    Rating addDislike(Long profileId, Rating updatedRating, String userName) throws CustomException;

    Map<String, Object> getAndCountLikesByProfileId(Long id) throws CustomException;
}
