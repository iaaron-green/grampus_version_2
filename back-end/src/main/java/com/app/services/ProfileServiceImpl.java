package com.app.services;

import com.app.entities.Profile;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

   private ProfileRepository profileRepository;
   private UserRepository userRepository;

   @Override
   public <S extends Profile> S saveProfile(S entity) {
      return profileRepository.save(entity);
   }
}
