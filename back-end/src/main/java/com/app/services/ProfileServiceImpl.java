package com.app.services;

import com.app.entities.Profile;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

   @Autowired
   private ProfileRepository profileRepository;

   @Override
   public <S extends Profile> S saveProfile(S entity) {
      return profileRepository.save(entity);
   }
}
