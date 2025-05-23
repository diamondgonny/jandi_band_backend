package com.jandi.band_backend.club.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "동아리 일정 엔티티")
@Entity
@Table(name = "club_event")
@Getter
@Setter
@NoArgsConstructor
public class ClubEvent {

    @Schema(description = "동아리 일정 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_event_id")
    private Integer id;

    @Schema(description = "일정을 생성한 동아리")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Schema(description = "일정 제목(예: 정기공연)", example = "2024 정기공연")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Schema(description = "일정 시작 일시", example = "2024-12-25T19:00:00")
    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Schema(description = "일정 종료 일시", example = "2024-12-25T22:00:00")
    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Schema(description = "장소 이름", example = "서울대학교 관악 SK관 컨벤션홀")
    @Column(name = "location", length = 255)
    private String location;

    @Schema(description = "상세 주소", example = "서울특별시 관악구 관악로 1")
    @Column(name = "address", length = 255)
    private String address;

    @Schema(description = "추가 설명", example = "올해 마지막 정기공연입니다. 많은 참석 부탁드립니다.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Schema(description = "일정 생성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private Users creator;

    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Schema(description = "일정 참가자 목록")
    @OneToMany(mappedBy = "clubEvent")
    private List<ClubEventParticipant> participants = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
