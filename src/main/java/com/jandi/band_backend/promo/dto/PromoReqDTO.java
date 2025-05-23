package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.Promo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "홍보 게시글 생성/수정 요청 DTO")
public class PromoReqDTO {
    @Schema(description = "클럽 ID", example = "1", required = true)
    @NotNull(message = "클럽 ID는 필수입니다")
    private Integer clubId;

    @Schema(description = "홍보 게시글 제목", example = "2024 겨울 정기공연", required = true, maxLength = 255)
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Schema(description = "입장료", example = "15000", required = false)
    private BigDecimal admissionFee;

    @Schema(description = "이벤트 일시", example = "2024-12-25T19:00:00", required = false)
    private LocalDateTime eventDatetime;

    @Schema(description = "공연 장소", example = "홍대 클럽 FF", required = false, maxLength = 255)
    @Size(max = 255, message = "장소는 255자를 초과할 수 없습니다")
    private String location;

    @Schema(description = "상세 주소", example = "서울시 마포구 와우산로 94", required = false, maxLength = 255)
    @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다")
    private String address;

    @Schema(description = "홍보 게시글 내용", example = "올해 마지막 정기공연에 여러분을 초대합니다!", required = false)
    private String description;

    @Schema(description = "홍보 게시글 상태", example = "ACTIVE", required = false)
    private Promo.PromoStatus status;
} 