package com.app.repository;

import com.app.DTO.DTOLikableProfile;
import com.app.configtoken.Constants;
import com.app.entities.Rating;
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

    @Query(
            value = "SELECT COUNT(rating_type) FROM " + Constants.DATABASE + ".ratings WHERE profile_id = ? AND rating_type = ?",
            nativeQuery = true)
    Long countRatingType(Long id, String ratingType);

}
    @Query("SELECT NEW com.app.DTO.DTOLikableProfile(r.profileRating.user.id, r.profileRating.user.fullName, r.profileRating.user.jobTitle, r.profileRating.profilePicture) " +
            "FROM  Rating r WHERE r.ratingType = :ratingType")
    Set<DTOLikableProfile> findProfileByRatingType(@Param("ratingType") String ratingType);