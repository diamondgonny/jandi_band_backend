package com.jandi.band_backend.poll.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "투표 후보 곡 엔티티")
@Entity
@Table(name = "poll_song")
@Getter
@Setter
@NoArgsConstructor
public class PollSong {
    
    @Schema(description = "투표 후보 곡 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_song_id")
    private Integer id;
    
    @Schema(description = "속한 투표 폼")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;
    
    @Schema(description = "곡 제목", example = "Bohemian Rhapsody")
    @Column(name = "song_name", nullable = false, length = 255)
    private String songName;
    
    @Schema(description = "아티스트/밴드 이름", example = "Queen")
    @Column(name = "artist_name", length = 255)
    private String artistName;
    
    @Schema(description = "유튜브 링크", example = "https://www.youtube.com/watch?v=fJ9rUzIMcZQ")
    @Column(name = "youtube_url", length = 255)
    private String youtubeUrl;
    
    @Schema(description = "추가 설명", example = "어려운 곡이지만 도전해볼만 합니다")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Schema(description = "곡 제안자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggester_user_id", nullable = false)
    private Users suggester;
    
    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Schema(description = "해당 곡에 대한 투표 목록")
    @OneToMany(mappedBy = "pollSong")
    private List<Vote> votes = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
