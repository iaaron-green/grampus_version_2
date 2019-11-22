package com.app.repository;

import com.app.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

   User findByUsername(String login);

   User getById(Long id);

   User findByActivationCode(String code);
}
