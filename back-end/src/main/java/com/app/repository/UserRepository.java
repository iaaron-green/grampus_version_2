package com.app.repository;

import com.app.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

   User findByUsername(String login);

   User getById(Long id);


   @Query(
           value = "SELECT id FROM users",
           nativeQuery = true)
   Set<Long> getAllId();
}
