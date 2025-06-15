package com.jandi.band_backend.search.service;

import com.jandi.band_backend.search.document.TeamDocument;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TeamSyncService {

    private final TeamSearchService teamSearchService;

    public TeamSyncService(TeamSearchService teamSearchService) {
        this.teamSearchService = teamSearchService;
    }

    /**
     * 팀 생성 시 Elasticsearch에 동기화
     */
    public void syncTeamCreate(Long teamId, String name, String description, String category, 
                              String status, Integer memberCount, Integer maxMembers) {
        TeamDocument teamDocument = new TeamDocument(
                teamId.toString(),
                name,
                description,
                category,
                status,
                memberCount,
                maxMembers,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        teamSearchService.saveTeam(teamDocument);
    }

    /**
     * 팀 업데이트 시 Elasticsearch에 동기화
     */
    public void syncTeamUpdate(Long teamId, String name, String description, String category, 
                              String status, Integer memberCount, Integer maxMembers) {
        TeamDocument teamDocument = new TeamDocument(
                teamId.toString(),
                name,
                description,
                category,
                status,
                memberCount,
                maxMembers,
                LocalDateTime.now(), // 원래 생성일을 유지하려면 조회 후 설정
                LocalDateTime.now()
        );
        
        teamSearchService.saveTeam(teamDocument);
    }

    /**
     * 팀 삭제 시 Elasticsearch에서 동기화
     */
    public void syncTeamDelete(Long teamId) {
        teamSearchService.deleteTeam(teamId.toString());
    }

    /**
     * 테스트용 샘플 데이터 생성
     */
    public void createSampleData() {
        List<TeamDocument> sampleTeams = List.of(
                new TeamDocument(
                        UUID.randomUUID().toString(),
                        "스터디 모임",
                        "함께 공부하는 개발자 모임입니다. 매주 모여서 알고리즘과 CS 공부를 합니다.",
                        "스터디",
                        "RECRUITING",
                        5,
                        10,
                        LocalDateTime.now().minusDays(7),
                        LocalDateTime.now()
                ),
                new TeamDocument(
                        UUID.randomUUID().toString(),
                        "풋살 동아리",
                        "매주 토요일 풋살을 즐기는 동아리입니다. 초보자도 환영합니다!",
                        "스포츠",
                        "ACTIVE",
                        12,
                        15,
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now().minusDays(1)
                ),
                new TeamDocument(
                        UUID.randomUUID().toString(),
                        "독서 클럽",
                        "한 달에 한 권씩 책을 읽고 토론하는 독서 모임입니다.",
                        "문화",
                        "RECRUITING",
                        8,
                        12,
                        LocalDateTime.now().minusDays(14),
                        LocalDateTime.now().minusDays(2)
                ),
                new TeamDocument(
                        UUID.randomUUID().toString(),
                        "요리 동호회",
                        "다양한 요리를 배우고 함께 만들어 먹는 모임입니다.",
                        "취미",
                        "ACTIVE",
                        6,
                        8,
                        LocalDateTime.now().minusDays(21),
                        LocalDateTime.now().minusDays(3)
                ),
                new TeamDocument(
                        UUID.randomUUID().toString(),
                        "프로젝트 팀",
                        "웹 개발 프로젝트를 함께 진행할 팀원을 모집합니다. React, Spring Boot 경험자 우대",
                        "프로젝트",
                        "RECRUITING",
                        3,
                        6,
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusHours(12)
                )
        );

        sampleTeams.forEach(teamSearchService::saveTeam);
    }

    /**
     * 모든 검색 데이터 삭제 (테스트용)
     */
    public void clearAllData() {
        teamSearchService.findAll().forEach(team -> 
            teamSearchService.deleteTeam(team.getId())
        );
    }
} 