package com.app.services;

import com.app.entities.Profile;
import com.app.repository.ProfileRepository;
import com.app.util.Errors;
import com.app.util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ProfileServiceImpl implements ProfileService {

   @Value("${upload.path}")
   private String uploadPath;

   @Autowired
   private ProfileRepository profileRepository;

   @Override
   public <S extends Profile> S saveProfile(S entity) {
      return profileRepository.save(entity);
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
         else throw new CustomException("" + Errors.PROFILE_PICTURE_IS_NULL);
      }
      else throw new CustomException("" + Errors.PROFILE_NOT_EXIST);

      return profile;
   }


}
