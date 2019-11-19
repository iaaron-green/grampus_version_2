package com.app.repository;

import com.app.entities.Profile;
import com.app.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Id;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    //Optional<Rating> findById(Long id);

//    @Query("SELECT rating_source_username, rating_type, profile_id, COUNT(rating_type) as count FROM grampus.db.ratings u WHERE" +
//            " u.profile_id = :profile_id")
//    Rating findOne(@Param("profile_id") Integer profile_id);
//        }

    @Query(
            value = "SELECT * FROM grampus_db.ratings",
            nativeQuery = true)
    List<Rating> findAllRatingById();
}