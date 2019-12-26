package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOProfile;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.enums.RatingSortParam;
import com.app.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.List;

@Service
public interface ProfileService {
    <S extends Profile> S saveProfile(S entity);

    DTOProfile getDTOProfileById(Long id, User currentUser) throws CustomException;

    Boolean updateProfile(DTOProfile profile, User currentUser);

    void saveProfilePhoto(MultipartFile file, Long id, User currentUser) throws CustomException;

    List<Profile> getAllProfiles() throws CustomException;

    List<DTOLikableProfile> getAllProfilesForRating(User currentUser, String searchParam, Integer page, Integer size, RatingSortParam sortParam, Mark ratingType) throws CustomException;

    Boolean changeSubscription(Long profileId, User currentUser) throws CustomException;

    String saveImgInFtp(MultipartFile file, String directory) throws CustomException;

}
