package com.app.repository;

import com.app.configtoken.Constants;
import com.app.entities.Rating;
import org.aspectj.apache.bcel.classfile.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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