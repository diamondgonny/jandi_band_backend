package com.jandi.band_backend.promo.service;

import com.jandi.band_backend.manage.entity.ReportReason;
import com.jandi.band_backend.manage.repository.ReportReasonRepository;
import com.jandi.band_backend.promo.dto.PromoReportReqDTO;
import com.jandi.band_backend.promo.dto.PromoReportRespDTO;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoReport;
import com.jandi.band_backend.promo.repository.PromoReportRepository;
import com.jandi.band_backend.promo.repository.PromoRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromoReportService {

    private final PromoReportRepository promoReportRepository;
    private final PromoRepository promoRepository;
    private final UserRepository userRepository;
    private final ReportReasonRepository reportReasonRepository;

    public void createPromoReport(PromoReportReqDTO request, Integer reporterUserId) {
        Users reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

// Promo 엔티티 조회
        Promo promo = promoRepository.findById(request.getPromoId())
                .orElseThrow(() -> new RuntimeException("해당 공연 홍보를 찾을 수 없습니다."));

// ReportReason 엔티티 조회
        ReportReason reportReason = reportReasonRepository.findById(request.getReportReasonId())
                .orElseThrow(() -> new RuntimeException("해당 신고 이유를 찾을 수 없습니다."));

// 신고 엔티티 생성 및 저장
        PromoReport promoReport = new PromoReport();
        promoReport.setPromo(promo);
        promoReport.setReporter(reporter);
        promoReport.setReportReason(reportReason);
        promoReport.setDescription(request.getDescription());

        promoReportRepository.save(promoReport);
    }

    public List<PromoReportRespDTO> getPromoReports() {
        return promoReportRepository.findAll().stream()
                .map(PromoReportRespDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
