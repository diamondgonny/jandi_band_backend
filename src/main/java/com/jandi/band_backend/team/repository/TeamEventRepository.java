package com.jandi.band_backend.team.repository;

import com.jandi.band_backend.team.entity.TeamEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamEventRepository extends JpaRepository<TeamEvent, Integer> {

    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.id = :id")
    Optional<TeamEvent> findByIdAndNotDeleted(@Param("id") Integer id);

    // 팀의 모든 연습 일정 조회
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.team.id = :teamId ORDER BY te.startDatetime ASC")
    Page<TeamEvent> findPracticeSchedulesByTeamId(@Param("teamId") Integer teamId, Pageable pageable);

    // 팀의 모든 연습 일정 조회 (List 반환)
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.team.id = :teamId")
    List<TeamEvent> findAllByTeamIdAndDeletedAtIsNull(@Param("teamId") Integer teamId);

    // 특정 팀의 날짜 범위 내 일정 조회 (캘린더용)
    @Query("SELECT te FROM TeamEvent te WHERE te.deletedAt IS NULL AND te.team.id = :teamId " +
           "AND ((te.startDatetime BETWEEN :startDate AND :endDate) " +
           "OR (te.endDatetime BETWEEN :startDate AND :endDate) " +
           "OR (te.startDatetime <= :startDate AND te.endDatetime >= :endDate)) " +
           "ORDER BY te.startDatetime ASC")
    List<TeamEvent> findTeamEventsByTeamIdAndDateRange(@Param("teamId") Integer teamId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
}
