package com.app.services;


import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTONewUser;
import com.app.exceptions.CustomException;

import java.util.List;

public interface UserService {

    DTONewUser saveUser(DTONewUser newUser) throws CustomException;
    List<DTOLikableProfile> findAllByJobTitle(String jobTitle);
}
