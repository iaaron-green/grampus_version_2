package com.app.repository;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   User findByUsername(String login);

   User getById(Long id);


   @Query(
           value = "SELECT id FROM users",
           nativeQuery = true)
   Set<Long> getAllId();

   @Query("SELECT NEW com.app.DTO.DTOLikableProfile(u.id, u.fullName, u.jobTitle, p.profilePicture) FROM User u, Profile p WHERE u.id NOT LIKE :id")
   Set<DTOLikableProfile> getLikeableProfiles(@Param("id") Long id);
}
