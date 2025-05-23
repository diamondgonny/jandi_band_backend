package com.jandi.band_backend.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamReqDTO {

    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(max = 100, message = "팀 이름은 최대 100자까지 입력 가능합니다.")
    private String name;
}
