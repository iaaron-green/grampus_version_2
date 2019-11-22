package com.app.repository;

import com.app.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findById(Long profileId);

    Profile findOneById(Long profileId);

    List<Profile> findAll();

    Profile findProfileById(Long id);


}
