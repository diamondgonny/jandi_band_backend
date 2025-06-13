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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jandi.band_backend.global.dto.PagedRespDTO;

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

        Promo promo = promoRepository.findById(request.getPromoId())
                .orElseThrow(() -> new RuntimeException("해당 공연 홍보를 찾을 수 없습니다."));

        if (promo.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 게시물은 신고할 수 없습니다.");
        }

        if (promo.getCreator().getId().equals(reporterUserId)) {
            throw new RuntimeException("본인이 작성한 게시물은 신고할 수 없습니다.");
        }

        ReportReason reportReason = reportReasonRepository.findById(request.getReportReasonId())
                .orElseThrow(() -> new RuntimeException("해당 신고 이유를 찾을 수 없습니다."));

        PromoReport promoReport = new PromoReport();
        promoReport.setPromo(promo);
        promoReport.setReporter(reporter);
        promoReport.setReportReason(reportReason);
        promoReport.setDescription(request.getDescription());

        promoReportRepository.save(promoReport);
    }

    public PagedRespDTO<PromoReportRespDTO> getPromoReports(Integer adminUserId, int page, int size, String sort) {
        Users adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!adminUser.getAdminRole().equals(Users.AdminRole.ADMIN)) {
            throw new RuntimeException("관리자만 신고 목록을 조회할 수 있습니다.");
        }

        Pageable pageable = createPageable(page, size, sort);
        Page<PromoReportRespDTO> promoReportPage = promoReportRepository.findAll(pageable)
                .map(PromoReportRespDTO::fromEntity);

        return PagedRespDTO.from(promoReportPage);
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
