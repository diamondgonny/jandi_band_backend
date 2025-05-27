package com.jandi.band_backend.promo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "공연 홍보 댓글 생성/수정 요청 DTO")
public class PromoCommentReqDTO {
    
    @Schema(description = "댓글 내용", example = "정말 기대되는 공연이네요! 꼭 보러 가겠습니다.", required = true)
    @NotBlank(message = "댓글 내용은 필수입니다")
    private String description;
} 