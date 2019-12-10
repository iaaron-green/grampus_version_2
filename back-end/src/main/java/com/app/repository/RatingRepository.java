package com.app.repository;

import com.app.DTO.DTOLikableProfile;
import com.app.config.Constants;
import com.app.entities.Rating;
import com.app.enums.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(
            value = "SELECT id, profile_id, rating_source_username,profile_id as user_id, rating_type, COUNT(rating_type) as raiting_count FROM " + Constants.DATABASE + ".ratings GROUP BY profile_id, rating_type",
            nativeQuery = true)
    List<Rating> findAllRatingById();

    @Query(value = "SELECT COUNT(rating_type) FROM " + Constants.DATABASE + ".ratings WHERE profile_id = ? AND rating_type = ?",
            nativeQuery = true)
    Long countRatingType(Long id, String ratingType);

    @Query(value = "SELECT * FROM " + Constants.DATABASE + ".ratings WHERE profile_id = ? AND rating_type is null",
            nativeQuery = true)
    Rating countRatingType(Long id);

    @Query("SELECT NEW com.app.DTO.DTOLikableProfile(r.profileRating.user.id, r.profileRating.user.fullName, r.profileRating.user.jobTitle, r.profileRating.profilePicture) " +
            "FROM  Rating r WHERE r.ratingType = :ratingType")
    Set<DTOLikableProfile> findProfileByRatingType(@Param("ratingType") Mark ratingType);

    @Query (value = "SELECT rating_type FROM " + Constants.DATABASE + ".ratings WHERE profile_id = ? AND rating_source_username = ?",
            nativeQuery = true)
    String checkLike(Long profileId, String currentUserEmail);

    @Query("SELECT COUNT(r.ratingType) FROM Rating r WHERE r.profileRating.user.id = :id AND r.ratingType NOT LIKE :ratingType")
    Long countProfileLikes(@Param("id")Long id, @Param("ratingType") Mark ratingType);

    @Query("SELECT COUNT(r.ratingType) FROM Rating r WHERE r.profileRating.user.id = :id AND r.ratingType LIKE :ratingType")
    Long countProfileDislikes(@Param("id")Long id, @Param("ratingType") Mark ratingType);

    @Query("SELECT DISTINCT NEW com.app.DTO.DTOLikableProfile (u.id, u.fullName, u.jobTitle, p.profilePicture, count(r.ratingType)) " +
            " FROM Rating r JOIN Profile p ON p.id = r.profileRating.user.id JOIN User u ON u.id = p.id AND r.profileRating.user.id IN :userIds WHERE r.ratingType NOT LIKE :ratingType OR r.ratingType IS NULL AND u.fullName LIKE :searchParam% OR u.jobTitle LIKE :searchParam%" +
            " GROUP BY u.id")
    Page<DTOLikableProfile> findSubscriptionsByParamWithoutDislike(@Param("userIds") Set<Long> userIds, @Param("ratingType") Mark ratingType, @Param("searchParam") String searchParam, Pageable p);

    @Query("SELECT DISTINCT NEW com.app.DTO.DTOLikableProfile (u.id, u.fullName, u.jobTitle, p.profilePicture, count(r.ratingType)) " +
            " FROM Rating r JOIN Profile p ON p.id = r.profileRating.user.id JOIN User u ON u.id = p.id AND r.profileRating.user.id IN :userIds WHERE r.ratingType LIKE :ratingType AND u.fullName LIKE :searchParam% OR u.jobTitle LIKE :searchParam%" +
            " GROUP BY u.id")
    Page<DTOLikableProfile> findSubscriptionsByParamAndRatingType(@Param("userIds") Set<Long> userIds, @Param("ratingType") Mark ratingType, @Param("searchParam") String searchParam, Pageable p);

    @Query("SELECT DISTINCT NEW com.app.DTO.DTOLikableProfile (u.id, u.fullName, u.jobTitle, p.profilePicture, count(r.ratingType)) " +
            " FROM Rating r JOIN Profile p ON p.id = r.profileRating.user.id JOIN User u ON u.id = p.id WHERE r.ratingType NOT LIKE :ratingType OR r.ratingType IS NULL AND u.fullName LIKE :searchParam% OR u.jobTitle LIKE :searchParam%" +
            " GROUP BY u.id")
    Page<DTOLikableProfile> findAllByParamWithoutDislike(@Param("ratingType") Mark ratingType, @Param("searchParam") String searchParam, Pageable p);

    @Query("SELECT DISTINCT NEW com.app.DTO.DTOLikableProfile (u.id, u.fullName, u.jobTitle, p.profilePicture, count(r.ratingType)) " +
            " FROM Rating r JOIN Profile p ON p.id = r.profileRating.user.id JOIN User u ON u.id = p.id WHERE r.ratingType LIKE :ratingType AND u.fullName LIKE :searchParam% OR u.jobTitle LIKE :searchParam%" +
            " GROUP BY u.id")
    Page<DTOLikableProfile> findAllByParamAndRatingType(@Param("ratingType") Mark ratingType, @Param("searchParam") String searchParam, Pageable p);

    @Query(value = "SELECT * FROM ratings WHERE comment is not NULL and profile_id = ?",
            nativeQuery = true)
    List<Rating> findAllCommentByProfileId(Long id);
}