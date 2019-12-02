package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOLikeDislike;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface RatingService {

    Boolean addLike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException, MessagingException;

    List<Rating> getAllAchieves() throws CustomException;

    Map<Long, Map<String, Long>> addInfoAchievement() throws CustomException;

    List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) throws CustomException;

    Boolean addDislike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException, MessagingException;

    Map<String, Object> getAndCountLikesByProfileId(Long id) throws CustomException;
}
