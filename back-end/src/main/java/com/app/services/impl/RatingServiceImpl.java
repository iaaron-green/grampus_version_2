package com.app.services.impl;

import com.app.entities.Rating;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.RatingService;
import com.app.web.model.AchievementData;
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

        listOfMarks.forEach(mark -> mapOfLikes.put(mark.toString(), ratingRepository.countRatingType(id, mark.toString())));

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
    public List<AchievementData> getUserRatingByType(Mark markType) {
        List<AchievementData> achievementData = new ArrayList<>();
        Set<AchievementData> userData = userRepository.getUserData();
        userData.stream().sorted().forEach(user -> {
            AchievementData achievement = new AchievementData();
            achievement.setProfilePhoto(user.getProfilePhoto());
            achievement.setUserId(user.getUserId());
            achievement.setUserName(user.getUserName());
            achievement.setCountLike(ratingRepository.countRatingType(user.getUserId(), markType.toString()));
            achievementData.add(achievement);
        });
        return achievementData;
    }


}
