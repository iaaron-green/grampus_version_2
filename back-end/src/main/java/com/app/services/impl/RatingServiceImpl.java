package com.app.services.impl;

import com.app.enums.Mark;
import com.app.repository.RatingRepository;
import com.app.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingRepository ratingRepository;


    @Override
    public String addAchievement(Long id) {

        Map<String, Object> achievments = new HashMap<>();
        List<Mark> positiveRating = Arrays.asList(Mark.values());

        positiveRating.forEach(mark -> achievments.put(mark.toString(), ratingRepository.countRatingType(id, mark.toString())));

        String s = achievments.toString();

        return s;

    }
}
