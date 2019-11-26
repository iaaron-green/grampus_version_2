package com.app.repository;

import com.app.DTO.DTOUserShortInfo;
import com.app.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

   User findByUsername(String login);

   User getById(Long id);


   @Query(
           value = "SELECT id FROM users",
           nativeQuery = true)
   Set<Long> getAllId();

   @Query(
           value = "SELECT id, full_name, profile_picture  FROM users JOIN  SELECT id,  profile_picture FROM profile",
           nativeQuery = true)
   Set<DTOUserShortInfo> getUserData();

   @Query(
           value = "SELECT *  FROM users WHERE job_title = ?",
           nativeQuery = true)
   Set<User> findAllUsersByJobTitle(String jobTitle);
}
