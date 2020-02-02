package com.app.controllers;

import com.app.DTO.DTONewsComment;
import com.app.configtoken.IAuthenticationFacade;
import com.app.exceptions.CustomException;
import com.app.repository.UserRepository;
import com.app.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserRepository userRepository;


    @Autowired
    IAuthenticationFacade authenticationFacade;

    @GetMapping("")
    public ResponseEntity<?>getAllDTONews(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size
                                          ) throws CustomException {
        return new ResponseEntity<>(newsService.getAllNews(authenticationFacade.getUser(), page, size).getContent() , HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> saveNews(@RequestParam("title") String title,
                                      @RequestParam("content") String content,
                                      @RequestParam(value = "file", required = false) MultipartFile file
                                      )  throws CustomException {
        newsService.saveDTONews(title,content,file, authenticationFacade.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/comment")
    public ResponseEntity<?> addComment(@Valid @RequestBody DTONewsComment dtoNewsComment
                                        ) throws CustomException {
        return new ResponseEntity<>(newsService.saveComment(dtoNewsComment.getId(), dtoNewsComment.getText(), authenticationFacade.getUser()),HttpStatus.OK) ;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long  id) throws CustomException {
        return new ResponseEntity<>(newsService.deleteNews(id, authenticationFacade.getUser()),HttpStatus.OK) ;
    }

    @GetMapping("/comment/{newsId}")
    public ResponseEntity<?>getCommentsByNewsId(@PathVariable Long  newsId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) throws CustomException {
        return new ResponseEntity<>(newsService.getAllCommentByNewsId(newsId, page, size).getContent(), HttpStatus.OK);
    };
}
