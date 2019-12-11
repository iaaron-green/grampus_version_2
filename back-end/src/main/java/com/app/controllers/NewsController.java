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
    public ResponseEntity<?>getAllDTONews(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                          Principal principal) throws CustomException {

        return new ResponseEntity<>(newsService.getAllNews(principal, page, size).getContent(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> saveNews(@RequestParam("title") String title,
                                      @RequestParam("content") String content,
                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                      Principal principal)  throws CustomException {
        newsService.saveDTONews(title,content,file, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/comment")
    public ResponseEntity<?> addComment(@RequestParam("id") Long id,
                                        @RequestParam("comment") String comment,
                                        Principal principal) throws CustomException {
        newsService.saveComment(id, comment, principal);
        return new ResponseEntity<>(HttpStatus.OK) ;
    }

    @GetMapping("/comment/{newsId}")
    public ResponseEntity<?>getCommentsByNewsId(@PathVariable Long  newsId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size){
        return new ResponseEntity<>(newsService.getAllCommentByNewsId(newsId, page, size).getContent(), HttpStatus.OK);
    };
}
