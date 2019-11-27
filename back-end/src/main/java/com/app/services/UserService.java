package com.app.services;


import com.app.DTO.DTOUserShortInfo;
import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.util.CustomException;

import java.util.List;
import java.util.Set;

public interface UserService {

    DTONewUser saveUser(User newUSer) throws CustomException;

    List<DTOUserShortInfo> findAllByJobTitle(String jobTitle);

}
