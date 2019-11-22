package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.Profile;
import com.app.util.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.app.entities.Rating;
import java.util.List;
import java.util.Optional;

import java.io.IOException;
import java.util.List;

@Service
public interface ProfileService {
    <S extends Profile> S saveProfile(S entity);

    Profile getProfileById(Long id) throws CustomException;

    Profile updateProfile(Profile updatedProfile, String principalName);

    void saveProfilePhoto(MultipartFile file, Long id) throws IOException, CustomException;

    List<Profile> getAllProfiles();

    List<DTOLikableProfile> getAllProfilesForLike(String principalName);

    Long count(String type);

    List<Rating> getAchives();
}
