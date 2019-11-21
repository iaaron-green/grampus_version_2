package com.app.services.impl;

import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.app.enums.Mark.INTROVERT;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public <S extends Profile> S saveProfile(S entity) {
        return profileRepository.save(entity);
    }

    @Override
    public Long count(String type) {
        return null;
    }


    public Optional<Profile> getProfileById(Long id) {
        Optional<Profile> profile = profileRepository.findById(id);
        if (profile != null) {

        } else return null;
        return profile;
    }

    @Override
    public Profile updateProfile(Profile updatedProfile, String principalName) {
        User currentUser = userRepository.findByUsername(principalName);

        fixUpdatedProfileUser(updatedProfile, currentUser);

        if (principalName.equals(updatedProfile.getUser().getUsername())) {

            Profile profileFromDB = profileRepository.findProfileById(updatedProfile.getId());

            if (updatedProfile.getInformation() != null) {
                profileFromDB.setInformation(updatedProfile.getInformation());
            }
            if (updatedProfile.getSkills() != null) {
                profileFromDB.setSkills(updatedProfile.getSkills());
            }
            if (updatedProfile.getUser().getJobTitle() != null) {
                profileFromDB.setSkills(updatedProfile.getSkills());
            }
            if (updatedProfile.getUser().getFullName() != null) {
                profileFromDB.setSkills(updatedProfile.getSkills());
            }


            return profileRepository.save(profileFromDB);
        }
        return new Profile();
    }

    private void fixUpdatedProfileUser(Profile updatedProfile, User currentUser) {
        if (!currentUser.equals(updatedProfile.getUser())) {
            updatedProfile.setUser(currentUser);
        }
    }

    public Profile findProfileByIdentifier(Long profileId) throws ProfileIdentifierException {
        if (profileId == null) {
            throw new ProfileIdentifierException("Profile ID '" + profileId + "' doesn't exists");
        }
        return profileRepository.findById(profileId).get();
    }

    public List<Rating> getAchives() {
        List<Rating> ratings = ratingRepository.findAllRatingById();
        return ratings;
    }


    public List<Rating> getAllAchives() {
        List<Rating> ratings = ratingRepository.groupAndCountRatingTypeById();
        return ratings;
    }



}


