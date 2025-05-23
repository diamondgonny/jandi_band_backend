package com.jandi.band_backend.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "사용자 시간표 엔티티")
@Entity
@Table(name = "user_timetable")
@Getter
@Setter
@NoArgsConstructor
public class UserTimetable {
    
    @Schema(description = "시간표 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_timetable_id")
    private Integer id;
    
    @Schema(description = "시간표 소유자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @Schema(description = "시간표 이름", example = "2024년 1학기 시간표")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Schema(description = "시간표 상세 데이터 담은 JSON", example = "{\"monday\": [{\"subject\": \"수학\", \"time\": \"09:00-10:30\"}]}")
    @Column(name = "user_timetable_data", nullable = false, columnDefinition = "json")
    private String timetableData;
    
    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
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
