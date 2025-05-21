package com.jandi.band_backend.poll.dto;

import com.jandi.band_backend.global.util.TimeUtil;
import com.jandi.band_backend.poll.entity.Poll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDetailRespDTO {
    private Integer id;
    private String title;
    private Integer clubId;
    private String clubName;
    private ZonedDateTime startDatetime;
    private ZonedDateTime endDatetime;
    private Integer creatorId;
    private String creatorName;
    private ZonedDateTime createdAt;
    private List<PollSongRespDTO> songs;
}
