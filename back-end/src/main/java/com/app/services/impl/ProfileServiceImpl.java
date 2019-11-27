package com.app.services.impl;

import com.app.DTO.DTOUserShortInfo;
import com.app.DTO.DTOProfile;
import com.app.configtoken.Constants;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import com.app.util.CustomException;
import com.app.util.Errors;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProfileServiceImpl implements ProfileService {


    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private RatingService ratingService;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository, RatingService ratingService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
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
    public DTOProfile getDTOProfileById(Long id) throws CustomException {
        Profile profileFromDB = profileRepository.findProfileById(id);
        if (profileFromDB != null) {
            DTOProfile dtoProfile = new DTOProfile();
            dtoProfile.setId(profileFromDB.getId());
            dtoProfile.setDislikes(profileFromDB.getDislikes());
            dtoProfile.setLikes(profileFromDB.getLikes());
            dtoProfile.setInformation(profileFromDB.getInformation());
            dtoProfile.setProfilePicture(profileFromDB.getProfilePicture());
            dtoProfile.setSkills(profileFromDB.getSkills());
            dtoProfile.setUser(profileFromDB.getUser());
            dtoProfile.setRatings(profileFromDB.getRatings());
            dtoProfile.setLikesNumber(ratingService.getAndCountLikesByProfileId(id));
            return dtoProfile;
        } else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);
    }

    @Override
    public Boolean updateProfile(DTOProfile profile, String principalName) {
        User currentUser = userRepository.findByUsername(principalName);

        if (currentUser != null) {
            Profile profileFromDB = profileRepository.findProfileById(currentUser.getId());
            if (profileFromDB != null){
                boolean isProfileUpdated = false;
                if (profile.getInformation() != null) {
                    profileFromDB.setInformation(profile.getInformation());
                    isProfileUpdated = true;
                }
                if (profile.getSkills() != null) {
                    profileFromDB.setSkills(profile.getSkills());
                    isProfileUpdated = true;
                }
                if (isProfileUpdated) {
                    profileRepository.save(profileFromDB);
                    return true;
                }
            } else return false;
        } else return false;
        return false;
    }


    @Override
    public Boolean saveProfilePhoto(MultipartFile file, Long id) throws CustomException {

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
                return true;
            } else throw new CustomException("" + Errors.PROFILE_PICTURE_IS_BAD);
        } else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);
    }

    public List<Profile> getAllProfiles() {

        return profileRepository.findAll();
    }

    public List<DTOUserShortInfo> getAllProfilesForLike(String principalName) {

        User currentUser = userRepository.findByUsername(principalName);

        Profile currentProfile = profileRepository.findOneById(currentUser.getId());

        List<DTOUserShortInfo> DTOUserShortInfos = new ArrayList<>();

        profileRepository.findAll().iterator()
                .forEachRemaining(profile -> {
                    if (CollectionUtils.isEmpty(profile.getRatings()) && !profile.equals(currentProfile)) {
                        getDTOLikableProfile(DTOUserShortInfos, profile, true);
                        return;
                    } else if (isProfileRatingIncludeLikeFromCurrentUser(currentProfile, profile)) {
                        getDTOLikableProfile(DTOUserShortInfos, profile, false);
                        return;
                    } else if (!CollectionUtils.isEmpty(profile.getRatings()) && !profile.equals(currentProfile)) {
                        getDTOLikableProfile(DTOUserShortInfos, profile, true);
                    }
                });
        return DTOUserShortInfos;
    }

    private void getDTOLikableProfile(List<DTOUserShortInfo> DTOUserShortInfos, Profile profile, boolean b) {
        DTOUserShortInfos.add(DTOUserShortInfo.builder()
                .profileId(profile.getId())
                .picture(profile.getProfilePicture())
                .fullName(profile.getUser().getFullName())
                .jobTitle(profile.getUser().getJobTitle())
                .isAbleToLike(b)
                .build());
    }

    private boolean isProfileRatingIncludeLikeFromCurrentUser(Profile currentProfile, Profile profile) {
        return !CollectionUtils.isEmpty(profile.getRatings()) && !profile.equals(currentProfile) &&
                profile.getRatings().stream()
                        .anyMatch(rating -> currentProfile.getUser().getUsername().equals(rating
                                .getRatingSourceUsername()));
    }
}
