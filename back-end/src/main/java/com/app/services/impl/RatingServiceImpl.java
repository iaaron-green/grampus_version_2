package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.RatingService;
import com.app.util.CustomException;
import com.app.util.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private MessageSource messageSource;

    public Rating addLike(Long profileId, Rating updatedRating, String userName) throws CustomException {

        Profile profile = profileRepository.findOneById(profileId);
        updatedRating.setProfileRating(profile);

        if (!userName.equals(profile.getUser().getEmail())) {
            Long profileLike = profile.getLikes();
            profile.setLikes(++profileLike);
            updatedRating.setRatingSourceUsername(userName);
            profileRepository.save(profile);
        }
        return ratingRepository.save(updatedRating);
    }

    public Rating addDislike(Long profileId, Rating updatedRating, String userName) throws CustomException {

        Profile profile = profileRepository.findOneById(profileId);
        updatedRating.setProfileRating(profile);

        if (!userName.equals(profile.getUser().getEmail())) {
            Long profileDislike = profile.getDislikes();
            profile.setDislikes(++profileDislike);
            updatedRating.setRatingSourceUsername(userName);
            profileRepository.save(profile);
        }
        return ratingRepository.save(updatedRating);
    }

    @Override
    public Map<String, Object> getAndCountLikesByProfileId(Long id) throws CustomException {

        Map<String, Object> mapOfLikes = new HashMap<>();
        List<Mark> listOfMarks = Arrays.asList(Mark.values());

        listOfMarks.forEach(mark -> mapOfLikes.put(mark.toString().toLowerCase(), ratingRepository.countRatingType(id, mark.toString().toLowerCase())));

        return mapOfLikes;

    }

    public List<Rating> getAllAchieves() throws CustomException {
        return ratingRepository.findAllRatingById();
    }

    @Override
    public Map<Long, Map<String, Long>> addInfoAchievement() throws CustomException {

        Map<Long, Map<String, Long>> userIdAndAchievments = new HashMap<>();
        List<Mark> positiveRating = Arrays.asList(Mark.values());

        Set<Long> userId = userRepository.getAllId();

        userId.forEach(user -> {
            Map<String, Long> achievements = new HashMap<>();
            positiveRating.forEach(mark -> achievements.put(mark.toString(), ratingRepository.countRatingType(user, mark.toString())));
            userIdAndAchievments.put(user, achievements);
        });

        String s = userIdAndAchievments.toString();
        System.out.println(s);

        return userIdAndAchievments;
    }

    //    @Override
//    public List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) throws CustomException {
//        List<DTOLikableProfile> achievementData = new ArrayList<>();
//        Set<DTOLikableProfile> profilesWithMark = ratingRepository.findProfileByRatingType(markType.name());
//        if (!CollectionUtils.isEmpty(profilesWithMark)) {
//            achievementData.addAll(profilesWithMark);
//        }
//        return achievementData;
//    }
    @Override
    public List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) throws CustomException {
        List<DTOLikableProfile> achievementData = new ArrayList<>();
        Set<Long> userIds = ratingRepository.getProfileIdsByRatingType(markType.name());
        Set<DTOLikableProfile> profilesWithMark = userRepository.findByIds(userIds);
        if (!CollectionUtils.isEmpty(profilesWithMark)){
            achievementData.addAll(profilesWithMark);
        } else {
            throw new CustomException(messageSource.getMessage("user.already.exist", null, LocaleContextHolder.getLocale()), Errors.MARKTYPE_NOT_EXIST);
        }
        return achievementData;
    }
}
