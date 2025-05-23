package com.jandi.band_backend.team.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "팀 일정 참가자 엔티티")
@Entity
@Table(name = "team_event_participant")
@Getter
@Setter
@NoArgsConstructor
public class TeamEventParticipant {
    
    @Schema(description = "팀 일정 참가자 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_event_participant_id")
    private Integer id;
    
    @Schema(description = "참여하는 팀 일정 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_event_id", nullable = false)
    private TeamEvent teamEvent;
    
    @Schema(description = "참가자 사용자 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
} 