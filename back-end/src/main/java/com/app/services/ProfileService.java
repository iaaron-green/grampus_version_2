package com.app.services;

import com.app.entities.Profile;

public interface ProfileService {
   public <S extends Profile> S saveProfile(S entity);
}
