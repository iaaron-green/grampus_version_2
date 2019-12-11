package com.app.services;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOProfile;
import com.app.entities.Profile;
import com.app.enums.Mark;
import com.app.enums.RatingSortParam;
import com.app.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
public interface ProfileService {
    <S extends Profile> S saveProfile(S entity);


    DTOProfile getDTOProfileById(Long id, Principal principal) throws CustomException;

    Boolean updateProfile(DTOProfile profile, String principalName);

    void saveProfilePhoto(MultipartFile file, Long id, Principal principal) throws CustomException;

    List<DTOLikableProfile> getAllProfilesForRating(String userName, String searchParam, Integer page, Integer size, RatingSortParam sortParam, Mark ratingType) throws CustomException;

}
