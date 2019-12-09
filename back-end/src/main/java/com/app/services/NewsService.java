package com.app.services;

import com.app.DTO.DTONews;
import com.app.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
public interface NewsService {

    void saveDTONews(String title, String content, MultipartFile file, Principal principal) throws CustomException;

    List<DTONews> getAllNews(Principal principal) throws CustomException;

    DTONews saveLike(Long idNews) throws CustomException;

    Boolean saveComment(Long id, String comment) throws CustomException;
}
