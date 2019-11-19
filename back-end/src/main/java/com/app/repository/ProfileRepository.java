package com.app.repository;

import com.app.entities.Profile;
import com.app.entities.Rating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

   Optional<Profile> findById(Long profileId);

   @Override
   Iterable<Profile> findAll();


//   Profile updateById(Long profileId);

//   Profile findProfileByUserName(String );

   Profile findProfileById(Long id);


}
