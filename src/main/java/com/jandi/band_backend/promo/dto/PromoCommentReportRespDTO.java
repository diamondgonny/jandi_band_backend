package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.PromoCommentReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "공연 홍보 댓글 신고 응답 DTO")
public class PromoCommentReportRespDTO {

    @Schema(description = "댓글 신고 ID", example = "1")
    private Integer id;

    @Schema(description = "신고된 댓글 ID", example = "15")
    private Integer promoCommentId;

    @Schema(description = "신고한 사용자 ID", example = "5")
    private Integer reporterUserId;

    @Schema(description = "신고 이유 ID", example = "3")
    private Integer reportReasonId;

    @Schema(description = "신고 상세 설명", example = "부적절한 내용이 포함되어 있습니다.")
    private String description;

    @Schema(description = "신고 생성일시", example = "2024-03-01T15:00:00")
    private LocalDateTime createdAt;

    public static PromoCommentReportRespDTO fromEntity(PromoCommentReport promoCommentReport) {
        PromoCommentReportRespDTO response = new PromoCommentReportRespDTO();
        response.setId(promoCommentReport.getId());
        response.setPromoCommentId(promoCommentReport.getPromoComment().getId());
        response.setReporterUserId(promoCommentReport.getReporter().getId());
        response.setReportReasonId(promoCommentReport.getReportReason().getId());
        response.setDescription(promoCommentReport.getDescription());
        response.setCreatedAt(promoCommentReport.getCreatedAt());
        return response;
    }
}
