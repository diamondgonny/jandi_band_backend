package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.Promo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Integer> {
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.club.id = :clubId")
    Page<Promo> findAllByClubId(@Param("clubId") Integer clubId, Pageable pageable);
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.eventDatetime >= :now ORDER BY p.eventDatetime ASC")
    Page<Promo> findUpcomingPromos(@Param("now") Instant now, Pageable pageable);
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.creator.id = :userId")
    Page<Promo> findAllByCreatorId(@Param("userId") Integer userId, Pageable pageable);
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.id = :id")
    Promo findByIdAndNotDeleted(@Param("id") Integer id);
} 