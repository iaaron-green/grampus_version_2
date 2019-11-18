package com.app.repository;

import com.app.entities.Achieve;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchieveRepository extends CrudRepository<Achieve, Long> {
    Optional<Achieve> findById(Long profileId);

    Optional<Achieve> findByName(String profileUserName);

    boolean existsById(Long profileId);


}
