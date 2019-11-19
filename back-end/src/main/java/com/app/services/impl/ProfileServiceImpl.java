package com.app.services.impl;

import com.app.entities.Profile;
import com.app.entities.User;
import com.app.repository.ProfileRepository;
import com.app.repository.UserRepository;
import com.app.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<Profile> getProfileById(Long id) {
        Optional<Profile> profile = profileRepository.findById(id);
        if (profile != null){

        } else return null;
        return profile;
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

    public Profile findProfileByIdentifier(Long profileId) throws ProfileIdentifierException {
        if (profileId == null) {
            throw new ProfileIdentifierException("Profile ID '" + profileId + "' doesn't exists");
        }
        return profileRepository.findById(profileId).get();
    }
}
