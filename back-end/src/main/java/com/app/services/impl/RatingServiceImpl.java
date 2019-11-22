package com.app.services.impl;

import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.enums.Mark;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.services.RatingService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ProfileRepository profileRepository;

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
    public String getAndCountLikesByProfileId(Long id) {

        Map<String, Object> mapOfLikes = new HashMap<>();
        List<Mark> listOfMarks = Arrays.asList(Mark.values());

        listOfMarks.forEach(mark -> mapOfLikes.put(mark.toString(), ratingRepository.countRatingType(id, mark.toString())));

        return new Gson().toJson(mapOfLikes);
    }
}
