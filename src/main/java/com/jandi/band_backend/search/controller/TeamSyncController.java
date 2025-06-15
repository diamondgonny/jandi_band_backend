package com.jandi.band_backend.search.controller;

import com.jandi.band_backend.search.service.TeamSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/search")
@Tag(name = "Team Search Admin", description = "팀 검색 관리 API (개발/테스트용)")
public class TeamSyncController {

    private final TeamSyncService teamSyncService;

    public TeamSyncController(TeamSyncService teamSyncService) {
        this.teamSyncService = teamSyncService;
    }

    @PostMapping("/teams/sample-data")
    @Operation(summary = "샘플 데이터 생성", description = "테스트용 샘플 팀 데이터를 생성합니다.")
    public ResponseEntity<String> createSampleData() {
        teamSyncService.createSampleData();
        return ResponseEntity.ok("샘플 데이터가 생성되었습니다.");
    }

    @DeleteMapping("/teams/all")
    @Operation(summary = "모든 검색 데이터 삭제", description = "Elasticsearch의 모든 팀 데이터를 삭제합니다.")
    public ResponseEntity<String> clearAllData() {
        teamSyncService.clearAllData();
        return ResponseEntity.ok("모든 검색 데이터가 삭제되었습니다.");
    }

    @PostMapping("/teams/sync")
    @Operation(summary = "팀 데이터 동기화", description = "특정 팀을 Elasticsearch에 동기화합니다.")
    public ResponseEntity<String> syncTeam(
            @RequestParam Long teamId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String status,
            @RequestParam Integer memberCount,
            @RequestParam Integer maxMembers) {
        
        teamSyncService.syncTeamCreate(teamId, name, description, category, status, memberCount, maxMembers);
        return ResponseEntity.ok("팀 데이터가 동기화되었습니다.");
    }
} 