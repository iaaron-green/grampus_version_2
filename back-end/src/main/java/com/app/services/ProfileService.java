package com.app.services;

import com.app.entities.Profile;
import com.app.entities.Rating;

import java.util.List;
import java.util.Optional;

public interface ProfileService {
    <S extends Profile> S saveProfile(S entity);

//    public Profile getUserProfile(String userName);

    Long count(String type);
    Profile updateProfile(Profile updatedProfile, String principalName);
    List<Rating> getAchives();
    Optional<Profile> getProfileById(Long id);
}
