package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.PromoReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "공연 홍보 신고 응답 DTO")
public class PromoReportRespDTO {

    @Schema(description = "공연 홍보 신고 ID", example = "1")
    private Integer id;

    @Schema(description = "신고된 공연 홍보 ID", example = "10")
    private Integer promoId;

    @Schema(description = "신고된 공연 홍보 제목", example = "밴드 공연 홍보")
    private String promoTitle;

    @Schema(description = "공연 홍보 작성자 ID", example = "2")
    private Integer promoCreatorId;

    @Schema(description = "공연 홍보 작성자 이름", example = "홍길동")
    private String promoCreatorName;

    @Schema(description = "신고한 사용자 ID", example = "5")
    private Integer reporterUserId;

    @Schema(description = "신고한 사용자 이름", example = "김철수")
    private String reporterUserName;

    @Schema(description = "신고 이유 ID", example = "3")
    private Integer reportReasonId;

    @Schema(description = "신고 이유 코드", example = "HARASSMENT")
    private String reportReasonCode;

    @Schema(description = "신고 상세 설명", example = "부적절한 내용이 포함되어 있습니다.")
    private String description;

    @Schema(description = "신고 생성일시", example = "2024-03-01T15:00:00")
    private LocalDateTime createdAt;

    public static PromoReportRespDTO fromEntity(PromoReport promoReport) {
        PromoReportRespDTO response = new PromoReportRespDTO();
        response.setId(promoReport.getId());
        response.setPromoId(promoReport.getPromo().getId());
        response.setPromoTitle(promoReport.getPromo().getTitle());
        response.setPromoCreatorId(promoReport.getPromo().getCreator().getId());
        response.setPromoCreatorName(promoReport.getPromo().getCreator().getNickname());
        response.setReporterUserId(promoReport.getReporter().getId());
        response.setReporterUserName(promoReport.getReporter().getNickname());
        response.setReportReasonId(promoReport.getReportReason().getId());
        response.setReportReasonCode(promoReport.getReportReason().getCode());
        response.setDescription(promoReport.getDescription());
        response.setCreatedAt(promoReport.getCreatedAt());
        return response;
    }
}
