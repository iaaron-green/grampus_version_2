package com.app.controllers;

import com.app.DTO.DTOComment;
import com.app.DTO.DTONews;
import com.app.entities.News;
import com.app.exceptions.CustomException;
import com.app.mq.Producer;
import com.app.repository.UserRepository;
import com.app.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Producer producer;

    @GetMapping("")
    public ResponseEntity<?>getAllDTONews(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                          Principal principal) throws CustomException {

       // return new ResponseEntity<>(newsService.getAllNews(principal, page, size).getContent(), HttpStatus.OK);
        return new ResponseEntity<>(newsService.getAllNews(principal, page, size).getContent() , HttpStatus.OK);
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
    public ResponseEntity<?> addComment(@Valid @RequestBody DTOComment dtoComment,
                                        Principal principal) throws CustomException {
        return new ResponseEntity<>(newsService.saveComment(dtoComment.getId(), dtoComment.getText(), principal),HttpStatus.OK) ;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long  id, Principal principal) throws CustomException {
        return new ResponseEntity<>(newsService.deleteNews(id, principal),HttpStatus.OK) ;
    }

    @GetMapping("/comment/{newsId}")
    public ResponseEntity<?>getCommentsByNewsId(@PathVariable Long  newsId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) throws CustomException {
        return new ResponseEntity<>(newsService.getAllCommentByNewsId(newsId, page, size).getContent(), HttpStatus.OK);
    };
}
