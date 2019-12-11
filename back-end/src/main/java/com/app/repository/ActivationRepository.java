package com.app.repository;

import com.app.entities.ActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationRepository extends JpaRepository<ActivationCode, Long> {
    ActivationCode findByUserId(Long id);
}
