package com.app.services;

import com.app.entities.Profile;
import com.app.util.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ProfileService {

   <S extends Profile> S saveProfile(S entity);
   Profile saveProfilePhoto(MultipartFile file, Long id) throws IOException, CustomException;

}
