package com.jandi.band_backend.club.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "동아리 일정 참가자 엔티티")
@Entity
@Table(name = "club_event_participant", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"club_event_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class ClubEventParticipant {

    @Schema(description = "동아리 일정 참가자 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_event_participant_id")
    private Integer id;

    @Schema(description = "동아리 일정 ID")
    @ManyToOne
    @JoinColumn(name = "club_event_id", nullable = false)
    private ClubEvent clubEvent;

    @Schema(description = "참가자 ID")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
