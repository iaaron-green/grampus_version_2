package com.app.services;


import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTONewUser;
import com.app.exceptions.CustomException;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    DTONewUser saveUser(DTONewUser newUser) throws CustomException, MessagingException;

    List<DTOLikableProfile> findAllByJobTitle(String jobTitle, int page, int size);

}
