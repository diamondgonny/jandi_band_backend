package com.jandi.band_backend.promo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "공연 홍보 생성/수정 요청 DTO")
public class PromoReqDTO {
    
    @Schema(description = "팀명 (필수)", example = "락밴드 팀", required = true, maxLength = 255)
    @NotBlank(message = "팀명은 필수입니다")
    @Size(max = 255, message = "팀명은 255자를 초과할 수 없습니다")
    private String teamName;

    @Schema(description = "공연 제목", example = "락밴드 동아리 정기공연", required = true, maxLength = 255)
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Schema(description = "입장료 (원)", example = "10000")
    private BigDecimal admissionFee;

    @Schema(description = "공연 일시", example = "2024-03-15T19:00:00")
    private LocalDateTime eventDatetime;

    @Schema(description = "공연 장소명", example = "홍대 클럽", maxLength = 255)
    @Size(max = 255, message = "장소는 255자를 초과할 수 없습니다")
    private String location;

    @Schema(description = "상세 주소", example = "서울시 마포구 홍익로 123", maxLength = 255)
    @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다")
    private String address;

    @Schema(description = "공연 설명", example = "락밴드 동아리의 정기 공연입니다. 다양한 장르의 음악을 선보일 예정입니다.")
    private String description;
} 