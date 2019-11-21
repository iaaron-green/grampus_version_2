package com.app.services;

import com.app.entities.Profile;

public interface ProfileService {
    public <S extends Profile> S saveProfile(S entity);

//    public Profile getUserProfile(String userName);


    public Long count(String type);
    public Profile updateProfile(Profile updatedProfile, String principalName);
}
