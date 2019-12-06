package com.app.repository;

import com.app.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findById(Long profileId);

    Profile findOneById(Long profileId);

    List<Profile> findAll();

    Profile findProfileById(Long id);

    @Query(
            value = "SELECT profile_id FROM ratings WHERE rating_source_username = ?",
            nativeQuery = true)
    Set<Long> getProfilesIdWithCurrentUserLike(String userEmail);

    @Query(
            value = "SELECT profile_id FROM user_subscriptions WHERE user_id = ?",
            nativeQuery = true)
    Set<Long> getUserSubscriptionsByUserId(Long id);
}
