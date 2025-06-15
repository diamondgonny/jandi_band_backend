package com.jandi.band_backend.search.controller;

import com.jandi.band_backend.search.document.TeamDocument;
import com.jandi.band_backend.search.service.TeamSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Team Search", description = "팀 검색 API")
public class TeamSearchController {

    private final TeamSearchService teamSearchService;

    public TeamSearchController(TeamSearchService teamSearchService) {
        this.teamSearchService = teamSearchService;
    }

    @GetMapping("/teams")
    @Operation(summary = "팀 통합 검색", description = "팀 이름 또는 설명으로 검색합니다.")
    public ResponseEntity<List<TeamDocument>> searchTeams(
            @Parameter(description = "검색어") @RequestParam String query) {
        List<TeamDocument> teams = teamSearchService.searchByNameOrDescription(query);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/name")
    @Operation(summary = "팀 이름 검색", description = "팀 이름으로 검색합니다.")
    public ResponseEntity<List<TeamDocument>> searchTeamsByName(
            @Parameter(description = "팀 이름") @RequestParam String name) {
        List<TeamDocument> teams = teamSearchService.searchByName(name);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/description")
    @Operation(summary = "팀 설명 검색", description = "팀 설명으로 검색합니다.")
    public ResponseEntity<List<TeamDocument>> searchTeamsByDescription(
            @Parameter(description = "팀 설명") @RequestParam String description) {
        List<TeamDocument> teams = teamSearchService.searchByDescription(description);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/category")
    @Operation(summary = "카테고리별 팀 검색", description = "카테고리별로 팀을 검색합니다.")
    public ResponseEntity<List<TeamDocument>> searchTeamsByCategory(
            @Parameter(description = "카테고리") @RequestParam String category) {
        List<TeamDocument> teams = teamSearchService.searchByCategory(category);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/status")
    @Operation(summary = "상태별 팀 검색", description = "상태별로 팀을 검색합니다.")
    public ResponseEntity<List<TeamDocument>> searchTeamsByStatus(
            @Parameter(description = "상태") @RequestParam String status) {
        List<TeamDocument> teams = teamSearchService.searchByStatus(status);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/members")
    @Operation(summary = "멤버 수 범위 검색", description = "멤버 수 범위로 팀을 검색합니다.")
    public ResponseEntity<List<TeamDocument>> searchTeamsByMemberCount(
            @Parameter(description = "최소 멤버 수") @RequestParam Integer minCount,
            @Parameter(description = "최대 멤버 수") @RequestParam Integer maxCount) {
        List<TeamDocument> teams = teamSearchService.searchByMemberCountRange(minCount, maxCount);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/all")
    @Operation(summary = "모든 팀 조회", description = "모든 팀을 조회합니다.")
    public ResponseEntity<List<TeamDocument>> getAllTeams() {
        List<TeamDocument> teams = teamSearchService.findAll();
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/teams")
    @Operation(summary = "팀 문서 저장", description = "팀 문서를 Elasticsearch에 저장합니다.")
    public ResponseEntity<TeamDocument> saveTeam(@RequestBody TeamDocument teamDocument) {
        TeamDocument savedTeam = teamSearchService.saveTeam(teamDocument);
        return ResponseEntity.ok(savedTeam);
    }

    @DeleteMapping("/teams/{id}")
    @Operation(summary = "팀 문서 삭제", description = "팀 문서를 Elasticsearch에서 삭제합니다.")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "팀 ID") @PathVariable String id) {
        teamSearchService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }
} 