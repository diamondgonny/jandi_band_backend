package com.jandi.band_backend.team.repository;

import com.jandi.band_backend.team.entity.TeamEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TeamEventRepository extends JpaRepository<TeamEvent, Integer> {
    
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.team.id = :teamId ORDER BY te.startDatetime ASC")
    Page<TeamEvent> findAllByTeamId(@Param("teamId") Integer teamId, Pageable pageable);
    
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.id = :id")
    Optional<TeamEvent> findByIdAndNotDeleted(@Param("id") Integer id);
    
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.startDatetime >= :startDate AND te.endDatetime <= :endDate AND te.team.id = :teamId ORDER BY te.startDatetime ASC")
    Page<TeamEvent> findByTeamIdAndDateRange(
            @Param("teamId") Integer teamId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable
    );
    
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.creator.id = :userId ORDER BY te.startDatetime ASC")
    Page<TeamEvent> findAllByCreatorId(@Param("userId") Integer userId, Pageable pageable);
    
    // 곡 연습 일정만 조회 (name에 " - "가 포함된 것들)
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.team.id = :teamId AND te.name LIKE '%-%' ORDER BY te.startDatetime ASC")
    Page<TeamEvent> findPracticeSchedulesByTeamId(@Param("teamId") Integer teamId, Pageable pageable);
    
    // 팀의 가장 최근 곡 연습 일정 조회 (현재 시간 기준으로 가장 가까운 미래 일정)
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.team.id = :teamId AND te.name LIKE '%-%' AND te.startDatetime >= :now ORDER BY te.startDatetime ASC LIMIT 1")
    Optional<TeamEvent> findLatestPracticeScheduleByTeamId(@Param("teamId") Integer teamId, @Param("now") LocalDateTime now);
} 