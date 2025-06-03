package com.jandi.band_backend.promo.service;

import com.jandi.band_backend.promo.dto.PromoCommentReportReqDTO;
import com.jandi.band_backend.promo.dto.PromoCommentReportRespDTO;
import com.jandi.band_backend.promo.entity.PromoComment;
import com.jandi.band_backend.promo.entity.PromoCommentReport;
import com.jandi.band_backend.promo.repository.PromoCommentReportRepository;
import com.jandi.band_backend.promo.repository.PromoCommentRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import com.jandi.band_backend.manage.entity.ReportReason;
import com.jandi.band_backend.manage.repository.ReportReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromoCommentReportService {

    private final PromoCommentReportRepository promoCommentReportRepository;
    private final PromoCommentRepository promoCommentRepository;
    private final UserRepository userRepository;
    private final ReportReasonRepository reportReasonRepository;

    public void createPromoCommentReport(PromoCommentReportReqDTO request, Integer reporterUserId) {
        Users reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        PromoComment promoComment = promoCommentRepository.findById(request.getPromoCommentId())
                .orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다."));

        ReportReason reportReason = reportReasonRepository.findById(request.getReportReasonId())
                .orElseThrow(() -> new RuntimeException("해당 신고 이유를 찾을 수 없습니다."));

        PromoCommentReport promoCommentReport = new PromoCommentReport();
        promoCommentReport.setPromoComment(promoComment);
        promoCommentReport.setReporter(reporter);
        promoCommentReport.setReportReason(reportReason);
        promoCommentReport.setDescription(request.getDescription());

        promoCommentReportRepository.save(promoCommentReport);
    }
}
