package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOProfile;
import com.app.configtoken.Constants;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import com.app.services.RatingService;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;


@Service
public class ProfileServiceImpl implements ProfileService {


    private MessageSource messageSource;
    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private RatingService ratingService;
    private RatingRepository ratingRepository;

    @Autowired
    public ProfileServiceImpl(MessageSource messageSource, ProfileRepository profileRepository, UserRepository userRepository,
                              RatingService ratingService, RatingRepository ratingRepository) {
        this.messageSource = messageSource;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
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
    public DTOProfile getDTOProfileById(Long id, Principal principal) throws CustomException {

        if (id == null || id == 0) {
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

        Profile profileFromDB = profileRepository.findProfileById(id);
        if (profileFromDB != null) {
            User currentUser = userRepository.findByEmail(principal.getName());
            DTOProfile dtoProfile = new DTOProfile();
            dtoProfile.setId(profileFromDB.getId());
            dtoProfile.setDislikes(profileFromDB.getDislikes());
            dtoProfile.setLikes(profileFromDB.getLikes());
            dtoProfile.setSkype(profileFromDB.getSkype());
            dtoProfile.setPhone(profileFromDB.getPhone());
            dtoProfile.setTelegram(profileFromDB.getTelegram());
            dtoProfile.setProfilePicture(profileFromDB.getProfilePicture());
            dtoProfile.setSkills(profileFromDB.getSkills());
            dtoProfile.setCountry(profileFromDB.getCountry());
            dtoProfile.setEmail(profileFromDB.getUser().getEmail());
            dtoProfile.setJobTitle(profileFromDB.getUser().getJobTitle());
            dtoProfile.setFullName(profileFromDB.getUser().getFullName());
            dtoProfile.setLikesNumber(ratingService.getAndCountLikesByProfileId(id));
            if (ratingRepository.checkLike(id, currentUser.getEmail()) != null) dtoProfile.setIsAbleToLike(false);
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
                if (profile.getSkype() != null) {
                    profileFromDB.setSkype(profile.getSkype());
                    isProfileUpdated = true;
                }
                if (profile.getPhone() != null) {
                    profileFromDB.setPhone(profile.getPhone());
                    isProfileUpdated = true;
                }
                if (profile.getTelegram() != null) {
                    profileFromDB.setTelegram(profile.getTelegram());
                    isProfileUpdated = true;
                }
                if (profile.getSkills() != null) {
                    profileFromDB.setSkills(profile.getSkills());
                    isProfileUpdated = true;
                }
                if (profile.getCountry() != null) {
                    profileFromDB.setCountry(profile.getCountry());
                    isProfileUpdated = true;
                }
                if (profile.getJobTitle() != null) {
                    profileFromDB.getUser().setJobTitle(profile.getJobTitle());
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
    public void saveProfilePhoto(MultipartFile file, Long id, Principal principal) throws CustomException {

        Long currentUserId = userRepository.findByEmail(principal.getName()).getId();

        if (!currentUserId.equals(id)) {
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

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

    public Page<DTOLikableProfile> getAllProfilesForLike(Principal principal, String searchParam, Integer page, Integer size) {

        Page<DTOLikableProfile> dtoProfiles;
        Set<Long> profilesIdWithLike;
        User user = userRepository.findByEmail(principal.getName());

        if (!searchParam.equals("")) {
            profilesIdWithLike = profileRepository.getProfilesIdWithCurrentUserLike(user.getEmail());
            dtoProfiles = userRepository.findByMask(user.getId(), searchParam, pageRequest(page, size));
            if (!CollectionUtils.isEmpty(profilesIdWithLike)) {
                return fillDTOLikableProfile(profilesIdWithLike, dtoProfiles);
            }

        } else {
            profilesIdWithLike = profileRepository.getProfilesIdWithCurrentUserLike(user.getEmail());
            dtoProfiles = userRepository.getLikeableProfiles(user.getId(), pageRequest(page, size));
            if (!CollectionUtils.isEmpty(profilesIdWithLike)) {
                return fillDTOLikableProfile(profilesIdWithLike, dtoProfiles);
            }
        }
        return dtoProfiles;
    }

    @Override
    public Boolean changeSubscription(Long profileId, Principal principal) throws CustomException {
        User currentUser = userRepository.findByEmail(principal.getName());
        Profile profile = profileRepository.findOneById(profileId);
        if(currentUser.getId().equals(profileId)){
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

        Set<Profile> subscribers = profile.getSubscribers();
        if(subscribers.contains(currentUser.getProfile())){
            subscribers.remove(currentUser.getProfile());
        } else {
            subscribers.add(currentUser.getProfile());

        }
        profileRepository.save(profile);
        return true;
    }

    private Page<DTOLikableProfile> fillDTOLikableProfile(Set<Long> profilesIdWithLike, Page<DTOLikableProfile> dtoLikableProfiles) {

        dtoLikableProfiles.forEach(profile -> {
            if (profilesIdWithLike.contains(profile.getId())) {
                profile.setIsAbleToLike(false);
            }
            else profile.setIsAbleToLike(true);
        });
        return dtoLikableProfiles;
    }

    private Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }



}
