package com.app.services.impl;

import com.app.configtoken.Constants;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.util.CustomException;
import com.app.util.Errors;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class ProfileServiceImpl implements ProfileService {


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
        } else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);
    }

    @Override
    public Profile updateProfile(Profile updatedProfile, String principalName) {
        User currentUser = userRepository.findByUsername(principalName);

         fixUpdatedProfileUser(updatedProfile, currentUser);

        if (principalName.equals(updatedProfile.getUser().getUsername())) {

            updatedProfile.setId(currentUser.getId());
            Profile profileFromDB = profileRepository.findProfileById(updatedProfile.getId());

            if (updatedProfile.getInformation() != null) {
                profileFromDB.setInformation(updatedProfile.getInformation());
            }
            if (updatedProfile.getSkills() != null) {
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
    public void saveProfilePhoto(MultipartFile file, Long id) throws CustomException {

        Profile profile = profileRepository.findOneById(id);
        if (profile != null) {
            if (file != null) {
                String contentType = file.getContentType();
                String profilePictureType = contentType.substring(contentType.indexOf("/") + 1);
                String pictureFullName = "img/" + profile.getId() + "." + profilePictureType;
                FTPClient client = new FTPClient();
                try {
                    client.connect(Constants.FTP_SERVER, Constants.FTP_PORT);
                    client.login("grampus", "password");
                    client.setFileType(FTPClient.BINARY_FILE_TYPE);
                    if (client.storeFile(pictureFullName, file.getInputStream())) {
                        client.logout();
                        client.disconnect();
                    }
                } catch (IOException e) {
                    throw new CustomException("" + Errors.FTP_CONNECTION_ERROR);
                }
                profile.setProfilePicture(Constants.FTP_IMG_LINK + pictureFullName);
                saveProfile(profile);
            } else throw new CustomException("" + Errors.PROFILE_PICTURE_IS_BAD);
        } else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);
    }
}