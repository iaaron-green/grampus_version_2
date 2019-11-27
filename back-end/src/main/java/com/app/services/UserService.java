package com.app.services;


import com.app.DTO.DTOUserShortInfo;
import com.app.DTO.DTONewUser;
import com.app.entities.User;
import com.app.util.CustomException;

import java.util.List;

public interface UserService {

    DTONewUser saveUser(User newUSer) throws CustomException;
}
