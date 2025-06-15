package com.jandi.band_backend.search.service;

import com.jandi.band_backend.search.document.TeamDocument;
import com.jandi.band_backend.search.repository.TeamSearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamSearchService {

    private final TeamSearchRepository teamSearchRepository;

    public TeamSearchService(TeamSearchRepository teamSearchRepository) {
        this.teamSearchRepository = teamSearchRepository;
    }

    /**
     * 팀 문서를 Elasticsearch에 저장
     */
    public TeamDocument saveTeam(TeamDocument teamDocument) {
        return teamSearchRepository.save(teamDocument);
    }

    /**
     * 팀 문서를 Elasticsearch에서 삭제
     */
    public void deleteTeam(String id) {
        teamSearchRepository.deleteById(id);
    }

    /**
     * ID로 팀 문서 조회
     */
    public Optional<TeamDocument> findById(String id) {
        return teamSearchRepository.findById(id);
    }

    /**
     * 모든 팀 문서 조회
     */
    public List<TeamDocument> findAll() {
        return (List<TeamDocument>) teamSearchRepository.findAll();
    }

    /**
     * 팀 이름으로 검색
     */
    public List<TeamDocument> searchByName(String name) {
        return teamSearchRepository.findByNameContaining(name);
    }

    /**
     * 팀 설명으로 검색
     */
    public List<TeamDocument> searchByDescription(String description) {
        return teamSearchRepository.findByDescriptionContaining(description);
    }

    /**
     * 팀 이름 또는 설명으로 통합 검색
     */
    public List<TeamDocument> searchByNameOrDescription(String query) {
        return teamSearchRepository.findByNameContainingOrDescriptionContaining(query, query);
    }

    /**
     * 카테고리별 검색
     */
    public List<TeamDocument> searchByCategory(String category) {
        return teamSearchRepository.findByCategory(category);
    }

    /**
     * 상태별 검색
     */
    public List<TeamDocument> searchByStatus(String status) {
        return teamSearchRepository.findByStatus(status);
    }

    /**
     * 멤버 수 범위로 검색
     */
    public List<TeamDocument> searchByMemberCountRange(Integer minCount, Integer maxCount) {
        return teamSearchRepository.findByMemberCountBetween(minCount, maxCount);
    }
} 