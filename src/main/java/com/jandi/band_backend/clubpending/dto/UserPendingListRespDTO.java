package com.jandi.band_backend.clubpending.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserPendingListRespDTO {
    private Integer userId;
    private String userNickname;
    private List<ClubPendingRespDTO> pendingApplications;
    private Integer totalCount;
}
