package com.app.services.impl;

import com.app.DTO.DTOComment;
import com.app.DTO.DTOLikeDislike;
import com.app.configtoken.Constants;
import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.ActivationService;
import com.app.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    private RatingRepository ratingRepository;
    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private MessageSource messageSource;
    private ActivationService activationService;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, ProfileRepository profileRepository, UserRepository userRepository,
                             MessageSource messageSource, ActivationService activationService) {
        this.ratingRepository = ratingRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.activationService = activationService;
    }

    @Override
    public Map<Mark, Object> getAndCountLikesByProfileId(Long id) {

        Map<Mark, Object> mapOfLikes = new HashMap<>();
        List<Mark> listOfMarks = Arrays.asList(Mark.values());

        listOfMarks.forEach(mark -> mapOfLikes.put(mark, ratingRepository.countRatingType(id, mark.toString())));

        return mapOfLikes;

    }

      private Profile checkProfile(Long profileId, DTOLikeDislike dtoLikeDislike) throws CustomException {

        if (profileId == null || profileId == 0) {
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

        Profile profile = profileRepository.findOneById(profileId);

        if (profile == null) {
            throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
        }

        if (dtoLikeDislike == null) {
            throw new CustomException(messageSource.getMessage("rating.type.is.empty", null, LocaleContextHolder.getLocale()), Errors.RATING_TYPE_IS_EMPTY);
        } else return profile;
    }

    public Boolean addRatingType(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws MessagingException, CustomException {

        Profile profile = checkProfile(profileId, dtoLikeDislike);
        User currentUser = userRepository.findByEmail(principal.getName());
        if (!currentUser.getId().equals(profile.getId()) && ratingRepository.checkLike(profile.getId(), currentUser.getEmail()) == null) {

            Rating dbRating = ratingRepository.countRatingType(profileId);
            if (dbRating == null) {
                dbRating = new Rating();
            }
            dbRating.setProfileRating(profile);
            dbRating.setRatingSourceUsername(currentUser.getEmail());
            dbRating.setRatingType(dtoLikeDislike.getRatingType());

            ratingRepository.save(dbRating);

            if (!dtoLikeDislike.getRatingType().equals(Mark.DISLIKE)) {
                Long likes = ratingRepository.countRatingType(profile.getId(), dtoLikeDislike.getRatingType().toString());
                if (likes % 5 == 0) {
                    activationService.sendMail(currentUser.getEmail(), Constants.ACHIEVE_NOTIFIC_MAIL_SUBJECT, Constants.ACHIEVE_NOTIFIC_MAIL_ARTICLE,
                            Constants.ACHIEVE_NOTIFIC_MAIL_MESSAGE + " \"" + dtoLikeDislike.getRatingType() + " \"");
                }
            }
            return true;
        } else return false;
    }
    public List<DTOComment> getAllComments(Long id){
        List<DTOComment> comments = new ArrayList<>();
        List<Rating> ratingComment = ratingRepository.findAllCommentByProfileId(id);
        ratingComment.forEach(user ->{
            DTOComment dtoComment = new DTOComment();
            dtoComment.setRatingType(user.getRatingType());
            dtoComment.setComments(user.getComment());
            dtoComment.setCreated_date(user.getCreated_date());
            comments.add(dtoComment);
        });
        return comments;
    }
}
