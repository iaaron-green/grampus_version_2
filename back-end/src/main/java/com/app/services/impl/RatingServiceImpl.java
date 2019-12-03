package com.app.services.impl;

import com.app.DTO.DTOLikableProfile;
import com.app.DTO.DTOLikeDislike;
import com.app.entities.Profile;
import com.app.entities.Rating;
import com.app.entities.User;
import com.app.enums.Mark;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ProfileRepository;
import com.app.repository.RatingRepository;
import com.app.repository.UserRepository;
import com.app.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    private RatingRepository ratingRepository;
    private ProfileRepository profileRepository;
    private UserRepository userRepository;
    private MessageSource messageSource;
    private JavaMailSender emailSender;
    private JmsTemplate jmsTemplate;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, ProfileRepository profileRepository, UserRepository userRepository,
                             MessageSource messageSource, JavaMailSender emailSender, JmsTemplate jmsTemplate) {
        this.ratingRepository = ratingRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.emailSender = emailSender;
        this.jmsTemplate = jmsTemplate;
    }

    public Boolean addLike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException, MessagingException {

        Profile profile = checkProfile(profileId, dtoLikeDislike);
        if (!dtoLikeDislike.getRatingType().equals(Mark.DISLIKE)) {
            Long profileLike = profile.getLikes();
            profile.setLikes(++profileLike);
        }
        return updateRatingAndProfile(profile, principal.getName(), dtoLikeDislike.getRatingType());
    }

    public Boolean addDislike(DTOLikeDislike dtoLikeDislike, Long profileId, Principal principal) throws CustomException, MessagingException {

        Profile profile = checkProfile(profileId, dtoLikeDislike);
        if (dtoLikeDislike.getRatingType().equals(Mark.DISLIKE)) {
            Long profileDislike = profile.getDislikes();
            profile.setDislikes(++profileDislike);
        }
        return updateRatingAndProfile(profile, principal.getName(), dtoLikeDislike.getRatingType());
    }

    @Override
    public Map<String, Object> getAndCountLikesByProfileId(Long id) {

        Map<String, Object> mapOfLikes = new HashMap<>();
        List<Mark> listOfMarks = Arrays.asList(Mark.values());

        listOfMarks.forEach(mark -> mapOfLikes.put(mark.toString().toLowerCase(), ratingRepository.countRatingType(id, mark.toString())));

        return mapOfLikes;

    }

    public List<Rating> getAllAchieves() {
        return ratingRepository.findAllRatingById();
    }

    @Override
    public Map<Long, Map<String, Long>> addInfoAchievement() {

        Map<Long, Map<String, Long>> userIdAndAchievments = new HashMap<>();
        List<Mark> positiveRating = Arrays.asList(Mark.values());

        Set<Long> userId = userRepository.getAllId();

        userId.forEach(user -> {
            Map<String, Long> achievements = new HashMap<>();
            positiveRating.forEach(mark -> achievements.put(mark.toString(), ratingRepository.countRatingType(user, mark.toString())));
            userIdAndAchievments.put(user, achievements);
        });

        return userIdAndAchievments;
    }

    @Override
    public List<DTOLikableProfile> getUserRatingByMarkType(Mark markType) {
        List<DTOLikableProfile> achievementData = new ArrayList<>();
        Set<DTOLikableProfile> profilesWithMark = ratingRepository.findProfileByRatingType(markType);
        if (!CollectionUtils.isEmpty(profilesWithMark)) {
            achievementData.addAll(profilesWithMark);
        }
        return achievementData;
    }

    private Profile checkProfile(Long profileId, DTOLikeDislike dtoLikeDislike) throws CustomException {

        if (profileId == null || profileId == 0) {
            throw new CustomException(messageSource.getMessage("wrong.profile.id", null, LocaleContextHolder.getLocale()), Errors.WRONG_PROFILE_ID);
        }

        Profile profile = profileRepository.findOneById(profileId);

        if (profile == null) {
            throw new CustomException(messageSource.getMessage("profile.not.exist", null, LocaleContextHolder.getLocale()), Errors.PROFILE_NOT_EXIST);
        }

        if (dtoLikeDislike == null) {
            throw new CustomException(messageSource.getMessage("rating.type.is.empty", null, LocaleContextHolder.getLocale()), Errors.RATING_TYPE_IS_EMPTY);
        } else return profile;
    }

    private Boolean updateRatingAndProfile(Profile profile, String userEmail, Mark ratingType) throws MessagingException {
        User currentUser = userRepository.findByEmail(userEmail);
        if (!currentUser.getId().equals(profile.getId()) && ratingRepository.checkLike(profile.getId(), currentUser.getEmail()) == null) {
            Rating updatedRating = new Rating();
            updatedRating.setProfileRating(profile);
            updatedRating.setRatingSourceUsername(currentUser.getEmail());
            updatedRating.setRatingType(ratingType);
            profileRepository.save(profile);
            ratingRepository.save(updatedRating);

            if (!ratingType.equals(Mark.DISLIKE)) {
                Long likes = ratingRepository.countRatingType(profile.getId(), ratingType.toString());
                if (likes % 1 == 0) {
                    jmsTemplate.convertAndSend("achieve", "You got new achievement " + "\"" +ratingType.toString() + "\"");

                    MimeMessage message = emailSender.createMimeMessage();
                    MimeMessageHelper helper = null;
                    try {
                        helper = new MimeMessageHelper(message, true, "utf-8");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                    String htmlMsg = "<h2><center>Congratulation!</center></h2>" +
                            "<p><center>You got new achievement " + "\"" +ratingType.toString() + "\"" + "<center></p>" +
                            "<img src='https://i.ibb.co/yNsKQ53/image.png'>";

                    message.setContent(htmlMsg, "text/html");

                    helper.setTo(profile.getUser().getEmail());

                    helper.setSubject("New Achievement(GRAMPUS)");

                    emailSender.send(message);
                }
            }
            return true;
        } else return false;
    }
}
