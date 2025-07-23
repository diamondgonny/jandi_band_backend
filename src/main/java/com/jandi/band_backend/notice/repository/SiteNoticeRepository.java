package com.jandi.band_backend.notice.repository;

import com.jandi.band_backend.notice.entity.SiteNotice;
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
public interface SiteNoticeRepository extends JpaRepository<SiteNotice, Integer> {

    // 전체 목록 조회 (소프트 삭제되지 않은 것만)
    @Query("SELECT sn FROM SiteNotice sn LEFT JOIN FETCH sn.creator WHERE sn.deletedAt IS NULL ORDER BY sn.createdAt DESC")
    Page<SiteNotice> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    // 단건 조회 (소프트 삭제되지 않은 것만)
    @Query("SELECT sn FROM SiteNotice sn LEFT JOIN FETCH sn.creator WHERE sn.id = :id AND sn.deletedAt IS NULL")
    Optional<SiteNotice> findByIdAndDeletedAtIsNull(@Param("id") Integer id);

    // 현재 활성화된 공지사항들 조회 (팝업용)
    @Query("SELECT sn FROM SiteNotice sn LEFT JOIN FETCH sn.creator WHERE sn.deletedAt IS NULL " +
           "AND sn.isPaused = false " +
           "AND sn.startDatetime <= :now " +
           "AND sn.endDatetime >= :now " +
           "ORDER BY sn.createdAt DESC")
    List<SiteNotice> findActiveNotices(@Param("now") LocalDateTime now);
}
