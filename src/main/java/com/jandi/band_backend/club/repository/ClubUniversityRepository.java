package com.jandi.band_backend.club.repository;

import com.jandi.band_backend.club.entity.ClubUniversity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubUniversityRepository extends JpaRepository<ClubUniversity, Long> {
    List<ClubUniversity> findByClubId(Long clubId);
    void deleteByClubId(Long clubId);
}
