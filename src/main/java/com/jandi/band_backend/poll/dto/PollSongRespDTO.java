package com.jandi.band_backend.poll.dto;

import com.jandi.band_backend.global.util.TimeUtil;
import com.jandi.band_backend.poll.entity.PollSong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollSongRespDTO {
    private Integer id;
    private Integer pollId;
    private String songName;
    private String artistName;
    private String youtubeUrl;
    private String description;
    private Integer suggesterId;
    private String suggesterName;
    private ZonedDateTime createdAt;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer cantCount;
    private Integer hajjCount;
}
