package com.app.services;


import com.app.DTO.DTONewUser;
import com.app.DTO.DTOUserShortInfo;
import com.app.util.CustomException;

import java.util.List;

public interface UserService {

    DTONewUser saveUser(DTONewUser newUser) throws CustomException;

    List<DTOUserShortInfo> findAllByJobTitle(String jobTitle);

}
