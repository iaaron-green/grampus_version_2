package com.app.repository;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.app.enums.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   User findByEmail(String email);

   User getById(Long id);


   @Query(
           value = "SELECT user_id FROM activation_code WHERE activate = 1",
           nativeQuery = true)
   Set<Long> getAllId();

   @Query("SELECT NEW com.app.DTO.DTOLikableProfile(u.id, u.fullName, u.jobTitle, p.profilePicture) FROM User u, Profile p WHERE u.id NOT LIKE :id AND u.id = p.id")
   Page<DTOLikableProfile> getLikeableProfiles(@Param("id") Long id, Pageable p);


//   @Query(
//           value = "SELECT *  FROM users WHERE job_title = ?",
//           nativeQuery = true)
//   Page<User> findAllUsersByJobTitle(String jobTitle, Pageable p);

    Set<User> findAllUsersByJobTitle(String jobTitle);

   @Query("SELECT NEW com.app.DTO.DTOLikableProfile(r.profileRating.user.id, r.profileRating.user.fullName, r.profileRating.user.jobTitle, r.profileRating.profilePicture) " +
           "FROM  Rating r WHERE r.ratingType IN :ratingTypes AND r.profileRating.user.id IN :userIds")
   List<DTOLikableProfile> findProfileByRatingType(@Param("ratingTypes") List<Mark> ratingTypes, @Param("userIds") Set<Long> userIds);

}
