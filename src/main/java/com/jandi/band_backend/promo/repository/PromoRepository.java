package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.Promo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Integer> {
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL")
    Page<Promo> findAllNotDeleted(Pageable pageable);
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.club.id = :clubId")
    Page<Promo> findAllByClubId(@Param("clubId") Integer clubId, Pageable pageable);
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.eventDatetime >= :now ORDER BY p.eventDatetime ASC")
    Page<Promo> findUpcomingPromos(@Param("now") LocalDateTime now, Pageable pageable);
    
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND p.creator.id = :userId")
    Page<Promo> findAllByCreatorId(@Param("userId") Integer userId, Pageable pageable);
    
    @Query("SELECT p FROM Promo p LEFT JOIN FETCH p.photos WHERE p.deletedAt IS NULL AND p.id = :id")
    Promo findByIdAndNotDeleted(@Param("id") Integer id);

    // 키워드 검색
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.teamName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Promo> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 필터링 (팀명과 클럽ID 모두 지원)
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL " +
           "AND (:startDate IS NULL OR p.eventDatetime >= :startDate) " +
           "AND (:endDate IS NULL OR p.eventDatetime <= :endDate) " +
           "AND (:teamName IS NULL OR LOWER(p.teamName) LIKE LOWER(CONCAT('%', :teamName, '%'))) " +
           "AND (:clubId IS NULL OR p.club.id = :clubId)")
    Page<Promo> filterPromos(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("teamName") String teamName,
        @Param("clubId") Integer clubId,
        Pageable pageable);

    // 팀명만으로 필터링 (기존 호환성)
    @Query("SELECT p FROM Promo p WHERE p.deletedAt IS NULL " +
           "AND (:startDate IS NULL OR p.eventDatetime >= :startDate) " +
           "AND (:endDate IS NULL OR p.eventDatetime <= :endDate) " +
           "AND (:teamName IS NULL OR LOWER(p.teamName) LIKE LOWER(CONCAT('%', :teamName, '%')))")
    Page<Promo> filterPromosByTeamName(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("teamName") String teamName,
        Pageable pageable);
} 