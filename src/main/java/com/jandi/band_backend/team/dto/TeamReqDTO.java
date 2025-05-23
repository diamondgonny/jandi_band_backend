package com.jandi.band_backend.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "팀 생성/수정 요청 DTO")
public class TeamReqDTO {

    @Schema(description = "팀 이름", example = "록밴드 팀", required = true, maxLength = 100)
    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(max = 100, message = "팀 이름은 최대 100자까지 입력 가능합니다.")
    private String name;
}
