package com.app.services;

import com.app.DTO.DTONewNews;
import com.app.entities.News;
import com.app.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
public interface NewsService {

    void saveDTONews(String title, String content, MultipartFile file, Principal principal) throws CustomException;

    List<DTONewNews> getAllNews(Principal principal) throws CustomException;
}
