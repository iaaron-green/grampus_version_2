package com.app.repository;

import com.app.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(
            value = "SELECT id, profile_id, profile_id as user_id, rating_type, COUNT(rating_type) as raiting_count FROM grampus_db.ratings GROUP BY profile_id, rating_type",
            nativeQuery = true)
    List<Rating> findAllRatingById();

    Long countRatingTypeById(Long id);

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
}