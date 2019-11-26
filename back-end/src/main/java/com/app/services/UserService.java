package com.app.services;


import com.app.DTO.DTOUserShortInfo;
import com.app.entities.User;

import java.util.List;

public interface UserService {

    User saveUser(User newUSer);

//    List<DTOUserShortInfo> findAllByJobTitle(String jobTitle);

}
