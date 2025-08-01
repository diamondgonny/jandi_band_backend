package com.jandi.band_backend.clubpending.repository;

import com.jandi.band_backend.clubpending.entity.ClubPending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubPendingRepository extends JpaRepository<ClubPending, Integer> {

    Optional<ClubPending> findByClubIdAndUserId(Integer clubId, Integer userId);

    @Query("SELECT cp FROM ClubPending cp WHERE cp.club.id = :clubId AND cp.status = 'PENDING' ORDER BY cp.appliedAt DESC")
    List<ClubPending> findPendingsByClubId(@Param("clubId") Integer clubId);

    @Modifying
    @Query("UPDATE ClubPending cp SET cp.status = 'EXPIRED', cp.processedAt = :now WHERE cp.status = 'PENDING' AND cp.expiresAt < :now")
    int bulkExpirePendingApplications(@Param("now") LocalDateTime now);
}
