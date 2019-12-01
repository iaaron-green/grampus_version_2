package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOLikeDislike;
import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.AchievementService;
import com.app.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    private RatingRepository ratingRepository;
    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private MessageSource messageSource;
    private AchievementService achievementService;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, ProfileRepository profileRepository, UserRepository userRepository,
                             MessageSource messageSource, AchievementService achievementService) {
        this.ratingRepository = ratingRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.achievementService = achievementService;
    }

    public Boolean addLike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException {

        if (profileId == null || profileId == 0) {
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

        Profile profile = profileRepository.findOneById(profileId);

        if (profile == null) {
            throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
        }

        User currentUser = userRepository.findByEmail(principal.getName());
        if (!currentUser.getId().equals(profileId) && ratingRepository.checkLike(profileId, currentUser.getEmail()) == null
        && !dtoLikeDislike.getRatingType().equals(Mark.DISLIKE)) {
            Long profileLike = profile.getLikes();
            profile.setLikes(++profileLike);
            Rating updatedRating = new Rating();
            updatedRating.setProfileRating(profile);
            updatedRating.setRatingSourceUsername(currentUser.getEmail());
            updatedRating.setRatingType(dtoLikeDislike.getRatingType());
            profileRepository.save(profile);
            ratingRepository.save(updatedRating);
            achievementService.recountUserAchievements(profileId, dtoLikeDislike.getRatingType());
            return true;
        } else return false;
    }

    public Boolean addDislike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException {

        if (profileId == null || profileId == 0) {
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

        Profile profile = profileRepository.findOneById(profileId);

        if (profile == null) {
            throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
        }

        User currentUser = userRepository.findByEmail(principal.getName());
        if (!currentUser.getId().equals(profileId) && ratingRepository.checkLike(profileId, currentUser.getEmail()) == null
        && dtoLikeDislike.getRatingType().equals(Mark.DISLIKE)) {
            Long profileDislike = profile.getDislikes();
            profile.setDislikes(++profileDislike);
            Rating updatedRating = new Rating();
            updatedRating.setProfileRating(profile);
            updatedRating.setRatingSourceUsername(currentUser.getEmail());
            updatedRating.setRatingType(dtoLikeDislike.getRatingType());
            profileRepository.save(profile);
            ratingRepository.save(updatedRating);
            return true;
        } else return false;
    }

    @Override
    public Map<String, Object> getAndCountLikesByProfileId(Long id) {

        Map<String, Object> mapOfLikes = new HashMap<>();
        List<Mark> listOfMarks = Arrays.asList(Mark.values());

        listOfMarks.forEach(mark -> mapOfLikes.put(mark.toString().toLowerCase(), ratingRepository.countRatingType(id, mark.toString())));

        return mapOfLikes;

    }

    public List<Rating> getAllAchieves() {
        return ratingRepository.findAllRatingById();
    }

    @Override
    public Map<Long, Map<String, Long>> addInfoAchievement() {

        Map<Long, Map<String, Long>> userIdAndAchievments = new HashMap<>();
        List<Mark> positiveRating = Arrays.asList(Mark.values());

        Set<Long> userId = userRepository.getAllId();

        userId.forEach(user -> {
            Map<String, Long> achievements = new HashMap<>();
            positiveRating.forEach(mark -> achievements.put(mark.toString(), ratingRepository.countRatingType(user, mark.toString())));
            userIdAndAchievments.put(user, achievements);
        });

        return userIdAndAchievments;
    }

    @Override
    public List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) {
        List<DTOLikableProfile> achievementData = new ArrayList<>();
        Set<DTOLikableProfile> profilesWithMark = ratingRepository.findProfileByRatingType(markType.name());
        if (!CollectionUtils.isEmpty(profilesWithMark)) {
            achievementData.addAll(profilesWithMark);
        }
        return achievementData;
    }
}
