package com.app.repository;

import com.app.DTO.DTOLikableProfile;
import com.app.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findById(Long profileId);

    Profile findOneById(Long profileId);

    List<Profile> findAll();

    Profile findProfileById(Long id);



    @Query(
            value = "SELECT COUNT(rating_type) FROM test_db.ratings WHERE profile_id = ? AND rating_type = ?",
            nativeQuery = true)
    List<DTOLikableProfile> getLikeableProfiles();

}
