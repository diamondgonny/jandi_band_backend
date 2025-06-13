package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.PromoCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCommentReportRepository extends JpaRepository<PromoCommentReport, Integer> {

    @Modifying
    @Query(value = "UPDATE promo_comment_report SET reporter_user_id = -1 WHERE reporter_user_id = :userId", nativeQuery = true)
    int anonymizeByReporterId(@Param("userId") Integer userId);
}
