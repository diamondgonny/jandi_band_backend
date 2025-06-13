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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jandi.band_backend.global.dto.PagedRespDTO;

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

        if (promoComment.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 댓글은 신고할 수 없습니다.");
        }

        if (promoComment.getCreator().getId().equals(reporterUserId)) {
            throw new RuntimeException("본인이 작성한 댓글은 신고할 수 없습니다.");
        }

        ReportReason reportReason = reportReasonRepository.findById(request.getReportReasonId())
                .orElseThrow(() -> new RuntimeException("해당 신고 이유를 찾을 수 없습니다."));

        PromoCommentReport promoCommentReport = new PromoCommentReport();
        promoCommentReport.setPromoComment(promoComment);
        promoCommentReport.setReporter(reporter);
        promoCommentReport.setReportReason(reportReason);
        promoCommentReport.setDescription(request.getDescription());

        promoCommentReportRepository.save(promoCommentReport);
    }


    public PagedRespDTO<PromoCommentReportRespDTO> getPromoCommentReports(Integer adminUserId, int page, int size, String sort) {
        Users adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!adminUser.getAdminRole().equals(Users.AdminRole.ADMIN)) {
            throw new RuntimeException("관리자만 신고 목록을 조회할 수 있습니다.");
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<PromoCommentReportRespDTO> promoCommentReportPage = promoCommentReportRepository.findAll(pageable)
                .map(PromoCommentReportRespDTO::fromEntity);

        return PagedRespDTO.from(promoCommentReportPage);
    }

    private Pageable createPageable(int page, int size, String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return PageRequest.of(page, size);
        }

        String[] sortParts = sort.split(",");
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, property));
    }

}
