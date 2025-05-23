package com.jandi.band_backend.team.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "팀 일정(합주‧연습) 엔티티")
@Entity
@Table(name = "team_event")
@Getter
@Setter
@NoArgsConstructor
public class TeamEvent {

    @Schema(description = "팀 일정(합주‧연습) ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_event_id")
    private Integer id;

    @Schema(description = "팀 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Schema(description = "일정 제목", example = "주간 합주")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Schema(description = "일정 시작 일시", example = "2024-01-01T19:00:00")
    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Schema(description = "일정 종료 일시", example = "2024-01-01T22:00:00")
    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Schema(description = "장소 이름", example = "홍대 연습실 A")
    @Column(name = "location", length = 255)
    private String location;

    @Schema(description = "상세 주소", example = "서울특별시 마포구 홍익로 123")
    @Column(name = "address", length = 255)
    private String address;

    @Schema(description = "파트 제외 표시", example = "NONE")
    @Enumerated(EnumType.STRING)
    @Column(name = "no_position")
    private NoPosition noPosition = NoPosition.NONE;

    @Schema(description = "추가 설명", example = "이번 주 새로운 곡 연습 예정입니다.")
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
    @OneToMany(mappedBy = "teamEvent")
    private List<TeamEventParticipant> participants = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Schema(description = "팀 이벤트 파트 제외 표시")
    public enum NoPosition {
        @Schema(description = "보컬")
        VOCAL, 
        @Schema(description = "기타")
        GUITAR, 
        @Schema(description = "키보드")
        KEYBOARD, 
        @Schema(description = "베이스")
        BASS, 
        @Schema(description = "드럼")
        DRUM, 
        @Schema(description = "제외 없음")
        NONE
    }
}
