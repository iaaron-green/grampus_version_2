package com.app.repository;

import com.app.entities.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

    Optional<Profile> findById(Long profileId);

    @Override
    Iterable<Profile> findAll();
}
