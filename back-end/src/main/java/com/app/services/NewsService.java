package com.app.services;

import com.app.DTO.DTONewsComment;
import com.app.DTO.DTONews;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Service
public interface NewsService {

    void saveDTONews(String title, String content, MultipartFile file, User currentUser) throws CustomException;

   // List<DTONews> getAllNews(Principal principal, Integer page, Integer size) throws CustomException;

    DTONewsComment saveComment(Long id, String comment, User currentUser) throws CustomException;

    Page<DTONews> getAllNews(User currentUser, Integer page, Integer size) throws CustomException;

    Page<DTONewsComment> getAllCommentByNewsId(Long id, Integer page, Integer size) throws CustomException;

    Boolean deleteNews(Long id, User currentUser) throws CustomException;
}
