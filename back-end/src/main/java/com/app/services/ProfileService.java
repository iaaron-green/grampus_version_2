package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOProfile;
import com.app.entities.Profile;
import com.app.util.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public interface ProfileService {
    <S extends Profile> S saveProfile(S entity);

    Profile getProfileById(Long id) throws CustomException;

    DTOProfile getDTOProfileById(Long id) throws CustomException;

    Profile updateProfile(Profile updatedProfile, String principalName);

    void saveProfilePhoto(MultipartFile file, Long id) throws IOException, CustomException;

    List<Profile> getAllProfiles() throws CustomException;

    Set<DTOLikableProfile> getAllProfilesForLike(Long id);


}
