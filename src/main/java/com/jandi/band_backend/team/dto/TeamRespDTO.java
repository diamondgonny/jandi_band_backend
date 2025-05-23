package com.jandi.band_backend.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRespDTO {

    private Integer id;
    
    private String name;
    
    private Integer creatorId;
    
    private String creatorName;
    
    private Integer memberCount;
    
    private LocalDateTime createdAt;
}
