package com.app.services;

import com.app.DTO.DTOComment;
import com.app.DTO.DTONews;
import com.app.exceptions.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Service
public interface NewsService {

    void saveDTONews(String title, String content, MultipartFile file, Principal principal) throws CustomException;

   // List<DTONews> getAllNews(Principal principal, Integer page, Integer size) throws CustomException;

    DTOComment saveComment(Long id, String comment, Principal principal) throws CustomException;

    Page<DTONews> getAllNews(Principal principal, Integer page, Integer size);

    Page<DTOComment> getAllCommentByNewsId(Long id, Integer page, Integer size);
}