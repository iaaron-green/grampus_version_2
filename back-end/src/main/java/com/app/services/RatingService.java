package com.app.services;

import com.app.DTO.DTOComment;
import com.app.DTO.DTOLikeDislike;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface RatingService {

    Boolean addRatingType(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws MessagingException, CustomException;

    Map<Mark, Object> getAndCountLikesByProfileId(Long id) throws CustomException;

    List<DTOComment> getAllComments(Long id);
}
