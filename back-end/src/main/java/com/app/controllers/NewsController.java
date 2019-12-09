package com.app.controllers;

import com.app.DTO.DTONews;
import com.app.exceptions.CustomException;
import com.app.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {
    @Autowired
    NewsService newsService;

    @GetMapping("")
    public ResponseEntity<?>getAllDTONews(Principal principal) throws CustomException {

        return new ResponseEntity<>(newsService.getAllNews(principal), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> saveNews(@RequestParam("title") String title,
                                      @RequestParam("content") String content,
                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                      Principal principal)  throws CustomException {
        if(title.length() > 100 && content.length() < 1000)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        newsService.saveDTONews(title,content,file, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<?> addlike(@RequestParam("id") Long id) throws CustomException {
        return new ResponseEntity<>(newsService.saveLike(id),HttpStatus.OK) ;
    }

    @PostMapping("/comment")
    public ResponseEntity<?> addComment(@RequestParam("id") Long id,
                                        @RequestParam("comment") String comment) throws CustomException {
        return new ResponseEntity<>(newsService.saveComment(id, comment),HttpStatus.OK) ;
    }
}
