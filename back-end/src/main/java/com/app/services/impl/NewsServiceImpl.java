package com.app.services.impl;

import com.app.DTO.DTONewNews;
import com.app.entities.News;
import com.app.configtoken.Constants;
import com.app.entities.Profile;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.NewsRepository;
import com.app.repository.UserRepository;
import com.app.services.NewsService;
import com.app.services.ProfileService;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProfileService profileService;

    @Override
    public void saveDTONews(String title, String content, MultipartFile file, Principal principal) throws CustomException {
        News newNews = new News();
        newNews.setTitle(title);
        newNews.setContent(content);

        if(file != null) {
            String contentType = file.getContentType();
            String profilePictureType = contentType.substring(contentType.indexOf("/") + 1);
            String pictureFullName = "news_img/" + UUID.randomUUID() + "." + profilePictureType;
            FTPClient client = new FTPClient();
            try {
                client.connect(Constants.FTP_SERVER, Constants.FTP_PORT);
                client.login("grampus", "password");
                client.setFileType(FTPClient.BINARY_FILE_TYPE);
                if (client.storeFile(pictureFullName, file.getInputStream())) {
                    client.logout();
                    client.disconnect();
                }
            } catch (IOException e) {
                throw new CustomException(messageSource.getMessage("ftp.connection.error", null, LocaleContextHolder.getLocale()), Errors.FTP_CONNECTION_ERROR);
            }
            newNews.setPicture(Constants.FTP_IMG_LINK + pictureFullName);
        }

        newNews.setDate(new Date(System.currentTimeMillis()));

        User user = userRepository.findByEmail(principal.getName());

        newNews.setProfileID(user.getId());
        newsRepository.save(newNews);

    }

    @Override
    public List<DTONewNews> getAllNews(Principal principal) throws CustomException {
        Iterable<News> allNews = newsRepository.findAll();
        List<DTONewNews> updatedNews = new ArrayList<>();
        for (News s : allNews) {
            User user =  userRepository.getById(s.getProfileID());
            if (user.getJobTitle() != null && (user.getJobTitle().equals("HR") || user.getJobTitle().equals("PM"))
                    || s.getProfileID().equals(userRepository.findByEmail(principal.getName()).getId())) {
                updatedNews.add(new DTONewNews(s.getTitle(), s.getContent(), s.getPicture(),
                        profileService.getProfileById(s.getProfileID()).getProfilePicture(),
                        userRepository.getById(s.getProfileID()).getFullName(),
                        s.getDate()));
                }
            }
        return updatedNews;
    }


}
