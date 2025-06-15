package com.jandi.band_backend.search.repository;

import com.jandi.band_backend.search.document.TeamDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamSearchRepository extends ElasticsearchRepository<TeamDocument, String> {
    
    // 팀 이름으로 검색
    List<TeamDocument> findByNameContaining(String name);
    
    // 팀 설명으로 검색
    List<TeamDocument> findByDescriptionContaining(String description);
    
    // 팀 이름 또는 설명으로 검색
    List<TeamDocument> findByNameContainingOrDescriptionContaining(String name, String description);
    
    // 카테고리별 검색
    List<TeamDocument> findByCategory(String category);
    
    // 상태별 검색
    List<TeamDocument> findByStatus(String status);
    
    // 멤버 수 범위로 검색
    List<TeamDocument> findByMemberCountBetween(Integer minCount, Integer maxCount);
} 