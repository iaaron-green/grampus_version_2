package com.app.services.impl;

import com.app.entities.Rating;
import com.app.entities.Profile;
import com.app.enums.Mark;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.RatingService;
import com.app.DTO.DTOAchievement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    UserRepository userRepository;

    public Rating addLike(Long profileId, Rating updatedRating, String userName){

        Profile profile = profileRepository.findOneById(profileId);
        updatedRating.setProfileRating(profile);

        if (!userName.equals(profile.getUser().getUsername())) {
            Long profileLike = profile.getLikes();
            profile.setLikes(++profileLike);
            updatedRating.setRatingSourceUsername(userName);
            profileRepository.save(profile);
        }
        return ratingRepository.save(updatedRating);
    }

    public Rating addDislike(Long profileId, Rating updatedRating, String userName){

        Profile profile = profileRepository.findOneById(profileId);
        updatedRating.setProfileRating(profile);

        if (!userName.equals(profile.getUser().getUsername())) {
            Long profileDislike = profile.getDislikes();
            profile.setDislikes(++profileDislike);
            updatedRating.setRatingSourceUsername(userName);
            profileRepository.save(profile);
        }
        return ratingRepository.save(updatedRating);
    }

    @Override
    public Map<String, Object> getAndCountLikesByProfileId(Long id) {

        Map<String, Object> mapOfLikes = new HashMap<>();
        List<Mark> listOfMarks = Arrays.asList(Mark.values());

        listOfMarks.forEach(mark -> mapOfLikes.put(mark.toString().toLowerCase(), ratingRepository.countRatingType(id, mark.toString().toLowerCase())));

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

        String s = userIdAndAchievments.toString();
        System.out.println(s);

        return userIdAndAchievments;
    }

    @Override
    public List<DTOAchievement> getUserRatingByType(Mark markType) {
        List<DTOAchievement> achievementData = new ArrayList<>();
        Set<Long> userIds = userRepository.getAllId();
        userIds.forEach(userId -> {
            DTOAchievement achievement = new DTOAchievement();
            achievement.setUserId(userId);
            achievement.setCountLike(ratingRepository.countRatingType(userId, markType.toString()));
            achievementData.add(achievement);
        });
        return achievementData;
    }


}
