package com.jandi.band_backend.manage.repository;

import com.jandi.band_backend.manage.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportReasonRepository extends JpaRepository<ReportReason, Integer> {
}
