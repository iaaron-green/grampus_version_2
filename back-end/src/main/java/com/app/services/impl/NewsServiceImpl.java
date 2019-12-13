package com.app.services.impl;

import com.app.DTO.DTOComment;
import com.app.DTO.DTONews;
import com.app.configtoken.Constants;
import com.app.entities.Comment;
import com.app.entities.News;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.CommentRepository;
import com.app.repository.NewsRepository;
import com.app.repository.UserRepository;
import com.app.services.NewsService;
import com.app.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private MessageSource messageSource;

    @Autowired
    private ProfileService profileService;

    @Override
    public void saveDTONews(String title, String content, MultipartFile file, Principal principal) throws CustomException {
        if (title.length() > 100 && content.length() < 1000)
            throw new CustomException(messageSource.getMessage("title.type.is.not.in.size", null, LocaleContextHolder.getLocale()), Errors.TITLE_IS_EMPTY);
        News newNews = new News();
        newNews.setTitle(title);
        newNews.setContent(content);

        String pictureFullName = profileService.saveImgInFtp(file, "news_img/" + UUID.randomUUID());
        if (pictureFullName != null) {
            newNews.setPicture(Constants.FTP_IMG_LINK + pictureFullName);
        }

        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyy");
        newNews.setDate(df.format(new Date().getTime()));

        newNews.setProfileID(userRepository.findByEmail(principal.getName()).getId());
        newsRepository.save(newNews);
    }

//    @Override
//    public List<DTONews> getAllNews(Principal principal, Integer page, Integer size) throws CustomException {
//
//        Page<News> allNews = newsRepository.findAllBy(pageRequest(page, size));
//        List<DTONews> updatedNews = new ArrayList<>();
//                allNews.forEach(news -> {
//                    User user = userRepository.getById(news.getProfileID());
//                    if (user.getJobTitle() != null && (user.getJobTitle().equals("HR") || user.getJobTitle().equals("PM"))
//                            || news.getProfileID().equals(userRepository.findByEmail(principal.getName()).getId())) {
//                        DTONews dtonews = new DTONews();
//                        List<Comment> c = newsRepository.findAllCommentById(1L);
//                        try {
//                            dtonews = DTONews.builder()
//                                    .id(user.getId())
//                                    .title(news.getTitle())
//                                    .content(news.getContent())
//                                    .picture(news.getPicture())
//                                    .imgProfile(profileService.getProfileById(news.getProfileID()).getProfilePicture())
//                                    .nameProfile(userRepository.getById(news.getProfileID()).getFullName())
//                                    .date(news.getDate())
//                                    .countOfLikes(news.getCountOfLikes())
//                                    .comment(c)
//                                    .build();
//                        } catch (CustomException e) {
//                            e.printStackTrace();
//                        }
//                        updatedNews.add(dtonews);
//                    }
//                });
//        return updatedNews;
//    }

    @Override
    public Page<DTONews> getAllNews(Principal principal, Integer page, Integer size) {
        User currentUser = userRepository.findByEmail(principal.getName());
        Set<Long> subscriptions = newsRepository.allSubscriptionId(currentUser.getId());
        Page<DTONews> news = newsRepository.news(subscriptions, pageRequest(page, size));
        return news;
    }

    @Override
    public Page<DTOComment> getAllCommentByNewsId(Long id, Integer page, Integer size) {
        return newsRepository.comments(id, pageRequest(page, size));
    }

    @Override
    public DTOComment saveComment(Long id, String textComment, Principal principal) throws CustomException {
        News s = newsRepository.findOneById(id);
        if (s == null)
            throw new CustomException(messageSource.getMessage("news.not.exist", null, LocaleContextHolder.getLocale()), Errors.NEWS_NOT_EXIST);


        User user = userRepository.findByEmail(principal.getName());
        Profile profile = user.getProfile();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyy");
        Comment newComment = new Comment(user.getFullName(), profile.getProfilePicture(), textComment, df.format(new Date().getTime()), s);

        return getDtoComment(commentRepository.save(newComment));
    }



    private DTONews getDtoNews(News s) throws CustomException {
        return new DTONews(s.getId(), s.getTitle(), s.getContent(), s.getPicture(),
                profileService.getProfileById(s.getProfileID()).getProfilePicture(),
                userRepository.getById(s.getProfileID()).getFullName(),
                s.getDate(), s.getProfileID());
    }

    private DTOComment getDtoComment(Comment c) throws CustomException {
        return new DTOComment(c.getId(), c.getImgProfile(),c.getCommentDate(),c.getText(), c.getFullName());
    }

    private Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }
}


