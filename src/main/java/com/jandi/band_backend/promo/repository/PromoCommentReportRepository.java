package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.PromoCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCommentReportRepository extends JpaRepository<PromoCommentReport, Integer> {
}
