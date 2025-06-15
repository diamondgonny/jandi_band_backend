package com.jandi.band_backend.search.repository;

import com.jandi.band_backend.search.document.PromoDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromoSearchRepository extends ElasticsearchRepository<PromoDocument, String> {
    
    // 제목으로 검색
    List<PromoDocument> findByTitleContaining(String title);
    
    // 팀 이름으로 검색
    List<PromoDocument> findByTeamNameContaining(String teamName);
    
    // 설명으로 검색
    List<PromoDocument> findByDescriptionContaining(String description);
    
    // 제목 또는 팀 이름 또는 설명으로 통합 검색
    List<PromoDocument> findByTitleContainingOrTeamNameContainingOrDescriptionContaining(
            String title, String teamName, String description);
    
    // 위치로 검색
    List<PromoDocument> findByLocationContaining(String location);
    
    // 주소로 검색
    List<PromoDocument> findByAddressContaining(String address);
    
    // 이벤트 날짜 범위로 검색
    List<PromoDocument> findByEventDatetimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 입장료 범위로 검색
    List<PromoDocument> findByAdmissionFeeBetween(Double minFee, Double maxFee);
    
    // 좋아요 수로 정렬하여 검색
    List<PromoDocument> findByOrderByLikeCountDesc();
    
    // 생성일 기준으로 정렬하여 검색
    List<PromoDocument> findByOrderByCreatedAtDesc();
} 