package com.app.repository;

import com.app.entities.Achieve;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchieveRepository extends CrudRepository<Achieve, Long> {
    Optional <Achieve> getAchieveByUserId(long id);
    void addAllAchieve(List<Achieve>achieveList);
}
