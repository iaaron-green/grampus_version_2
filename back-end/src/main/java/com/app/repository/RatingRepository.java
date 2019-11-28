package com.app.repository;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.Rating;
import com.app.enums.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(
            value = "SELECT id, profile_id, rating_source_username,profile_id as user_id, rating_type, COUNT(rating_type) as raiting_count FROM grampus_db.ratings GROUP BY profile_id, rating_type",
            nativeQuery = true)
    List<Rating> findAllRatingById();

    @Query(
            value = "SELECT id, profile_id, profile_id as user_id, rating_type, COUNT(rating_type) as raiting_count FROM grampus_db.ratings GROUP BY rating_type",
            nativeQuery = true)
    List<Rating> groupAndCountRatingTypeById();

    @Query(
            value = "SELECT COUNT(rating_type) FROM grampus_db.ratings WHERE profile_id = ? AND rating_type = ?",
            nativeQuery = true)
    Long countRatingType(Long id, String ratingType);


//    @Query(
//            value = "SELECT id, profile_id, profile_id as user_id, rating_type, COUNT(rating_type) as raiting_count FROM grampus_db.ratings GROUP BY rating_type",
//            nativeQuery = true)
    List<Rating> getAllByRatingType(String s);

    @Query(
            value = "SELECT rating_type FROM grampus_db.ratings WHERE rating_source_username = ? AND profile_id = ?",
            nativeQuery = true)
    List<String> getProfileRatingTypes (String username, Long profileId);

//    @Query(
//            value = "SELECT COUNT(rating_type) FROM grampus_db.ratings",
//            nativeQuery = true)
//    List<Rating> getAllRatingById ();
//@Query("SELECT NEW com.app.DTO.DTOLikableProfile(u.id, u.fullName, u.jobTitle, p.profilePicture) FROM User u, Profile p WHERE u.jobTitle LIKE :jobTitle AND u.id = p.id")
//DTOLikableProfile countRatingType(@Param("jobTitle") Mark jobtitle);

    @Query(
            value = "SELECT profile_id FROM grampus_db.ratings WHERE rating_type = ?",
            nativeQuery = true)
    Set<Long> getProfileIdsByRatingType(String ratingType);

    @Query("SELECT NEW com.app.DTO.DTOLikableProfile(r.profileRating.user.id, r.profileRating.user.fullName, r.profileRating.user.jobTitle, r.profileRating.profilePicture) " +
            "FROM  Rating r WHERE r.ratingType = :ratingType")
    Set<DTOLikableProfile> findProfileByRatingType(@Param("ratingType")String ratingType);
}