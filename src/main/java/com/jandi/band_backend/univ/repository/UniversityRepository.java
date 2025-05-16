package com.jandi.band_backend.univ.repository;

import com.jandi.band_backend.univ.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByName(String university);
}
