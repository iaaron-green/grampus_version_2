package com.app.services.impl;

import com.app.DTO.DTONews;
import com.app.configtoken.Constants;
import com.app.entities.Comment;
import com.app.entities.News;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.repository.CommentRepository;
import com.app.repository.NewsRepository;
import com.app.repository.UserRepository;
import com.app.services.NewsService;
import com.app.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    ProfileService profileService;

    @Override
    public void saveDTONews(String title, String content, MultipartFile file, Principal principal) throws CustomException {
        News newNews = new News();
        newNews.setTitle(title);
        newNews.setContent(content);

        String pictureFullName = profileService.saveImgInFtp(file, "news_img/" + UUID.randomUUID());
        if(pictureFullName != null)
            newNews.setPicture(Constants.FTP_IMG_LINK + pictureFullName);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyy");
        newNews.setDate(df.format(new Date().getTime()));

        newNews.setProfileID(userRepository.findByEmail(principal.getName()).getId());
        newNews.setCountOfLikes(0);
        newsRepository.save(newNews);
    }

    @Override
    public List<DTONews> getAllNews(Principal principal) throws CustomException {
        Iterable<News> allNews = newsRepository.findAll();
        List<DTONews> updatedNews = new ArrayList<>();
        for (News s : allNews) {
            User user = userRepository.getById(s.getProfileID());
            if (user.getJobTitle() != null && (user.getJobTitle().equals("HR") || user.getJobTitle().equals("PM"))
                    || s.getProfileID().equals(userRepository.findByEmail(principal.getName()).getId())) {
                updatedNews.add(getDtoNews(s));
            }
        }
        return updatedNews;
    }

    @Override
    public DTONews saveLike(Long id) throws CustomException {
        News s = newsRepository.findOneById(id);
        s.setCountOfLikes(s.getCountOfLikes() + 1);
        newsRepository.save(s);

        return getDtoNews(s);
    }

    @Override
    public Boolean saveComment(Long id, String comment) throws CustomException {
        News s = newsRepository.findOneById(id);
        Profile profile = profileService.getProfileById(id);
        User user = userRepository.getById(id);
        List<Comment> arr = s.getComment();
        Comment newComment = new Comment(user.getFullName(),profile.getProfilePicture(), comment);

        newComment.setNews(s);
        commentRepository.save(newComment);

        return true;
    }

    private DTONews getDtoNews(News s) throws CustomException {
        return new DTONews(s.getId(), s.getTitle(), s.getContent(), s.getPicture(),
                profileService.getProfileById(s.getProfileID()).getProfilePicture(),
                userRepository.getById(s.getProfileID()).getFullName(),
                s.getDate(), s.getCountOfLikes(), s.getComment());
    }
}


