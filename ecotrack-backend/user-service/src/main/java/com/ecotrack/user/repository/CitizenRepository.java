package com.ecotrack.user.repository;

import com.ecotrack.user.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByUserId(Long userId);
}
