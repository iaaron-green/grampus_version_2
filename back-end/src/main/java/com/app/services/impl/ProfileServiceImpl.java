package com.app.services.impl;

import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.util.CustomException;
import com.app.util.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Value("${upload.path}")
    private String uploadPath;

    private ProfileRepository profileRepository;
    private UserRepository userRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public <S extends Profile> S saveProfile(S entity) {
        return profileRepository.save(entity);
    }

    @Override
    public Profile getProfileById(Long id) throws CustomException {
        Profile profile = profileRepository.findProfileById(id);
        if (profile != null) {
            return profile;
        }
        else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);
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

    @Override
    public Profile saveProfilePhoto(MultipartFile file, Long id) throws IOException, CustomException {

        Profile profile = profileRepository.findOneById(id);
        if (profile != null) {
            if (file != null) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String contentType = file.getContentType();
                String pictureType = contentType.substring(contentType.indexOf("/")+1);
                String resultFileName = "picture" + profile.getId() + "." + pictureType;
                file.transferTo(new File(uploadPath + "/" + resultFileName));
                profile.setProfilePicture(resultFileName);
                saveProfile(profile);
            }
            else throw new CustomException("" + Errors.PROFILE_PICTURE_IS_BAD);
        }
        else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);

        return profile;
    }
}
