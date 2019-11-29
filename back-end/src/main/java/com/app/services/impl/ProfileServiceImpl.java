package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOProfile;
import com.app.configtoken.Constants;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class ProfileServiceImpl implements ProfileService {


    private MessageSource messageSource;
    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private RatingService ratingService;

    @Autowired
    public ProfileServiceImpl(MessageSource messageSource, ProfileRepository profileRepository, UserRepository userRepository, RatingService ratingService) {
        this.messageSource = messageSource;
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
        } else throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
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
            dtoProfile.setEmail(profileFromDB.getUser().getEmail());
            dtoProfile.setJobTitle(profileFromDB.getUser().getJobTitle());
            dtoProfile.setFullName(profileFromDB.getUser().getFullName());
            dtoProfile.setLikesNumber(ratingService.getAndCountLikesByProfileId(id));
            return dtoProfile;
        } else throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
    }

    @Override
    public Boolean updateProfile(DTOProfile profile, String principalName) {
        User currentUser = userRepository.findByEmail(principalName);

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
                    throw new CustomException(messageSource.getMessage("ftp.connection.error", null, LocaleContextHolder.getLocale()), Errors.FTP_CONNECTION_ERROR);
                }
                profile.setProfilePicture(Constants.FTP_IMG_LINK + pictureFullName);
                saveProfile(profile);
            } else throw new CustomException(messageSource.getMessage("picture.is.bad", null, LocaleContextHolder.getLocale()), Errors.PROFILE_PICTURE_IS_BAD);
        } else throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
    }

    public List<Profile> getAllProfiles()  {

        return profileRepository.findAll();
    }

    public Set<DTOLikableProfile> getAllProfilesForLike(String userName)  {

        User user = userRepository.findByEmail(userName);
        Set<Long> profilesIdWithLike;
        Set<DTOLikableProfile> dtoLikableProfiles = new HashSet<>();
        if (user != null) {
            profilesIdWithLike = profileRepository.getProfilesIdWithCurrentUserLike(userName);
            dtoLikableProfiles = userRepository.getLikeableProfiles(user.getId());
            if (!CollectionUtils.isEmpty(profilesIdWithLike) && !CollectionUtils.isEmpty(dtoLikableProfiles)){
                return fillDTOLikableProfile(profilesIdWithLike, dtoLikableProfiles);
            }
        }
        return dtoLikableProfiles;
    }

    private Set<DTOLikableProfile> fillDTOLikableProfile(Set<Long> profilesIdWithLike, Set<DTOLikableProfile> dtoLikableProfiles) {

        dtoLikableProfiles.forEach(profile -> {
            if (profilesIdWithLike.contains(profile.getId())) {
                profile.setIsAbleToLike(false);
            }
            else profile.setIsAbleToLike(true);
        });
        return dtoLikableProfiles;
    }
}
