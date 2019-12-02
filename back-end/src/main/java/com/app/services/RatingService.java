package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOLikeDislike;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface RatingService {

    Boolean addLike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException;

    List<Rating> getAllAchieves() throws CustomException;

    Map<Long, Map<Mark, Long>> addInfoAchievement() throws CustomException;

    List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) throws CustomException;

    Boolean addDislike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException;

    Map<Mark, Object> getAndCountLikesByProfileId(Long id) throws CustomException;

    //    List<DTOLikableProfile> findAllByAchievement(Mark mark);
    List<DTOLikableProfile> addDTOInfoAchievement();
}
