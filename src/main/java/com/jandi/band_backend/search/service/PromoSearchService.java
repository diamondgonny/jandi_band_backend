package com.jandi.band_backend.search.service;

import com.jandi.band_backend.search.document.PromoDocument;
import com.jandi.band_backend.search.repository.PromoSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PromoSearchService {

    private final PromoSearchRepository promoSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 공연 홍보 문서를 Elasticsearch에 저장
     */
    public PromoDocument savePromo(PromoDocument promoDocument) {
        return promoSearchRepository.save(promoDocument);
    }

    /**
     * 공연 홍보 문서를 Elasticsearch에서 삭제
     */
    public void deletePromo(String id) {
        promoSearchRepository.deleteById(id);
    }

    /**
     * ID로 공연 홍보 문서 조회
     */
    public Optional<PromoDocument> findById(String id) {
        try {
            return promoSearchRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 모든 공연 홍보 문서 조회
     */
    public List<PromoDocument> findAll() {
        try {
            Iterable<PromoDocument> all = promoSearchRepository.findAll();
            List<PromoDocument> result = new java.util.ArrayList<>();
            all.forEach(result::add);
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 키워드로 통합 검색 (제목, 팀 이름, 설명)
     */
    public Page<PromoDocument> searchByKeyword(String keyword, Pageable pageable) {
        try {
            Criteria criteria = new Criteria()
                    .or("title").contains(keyword)
                    .or("teamName").contains(keyword)
                    .or("description").contains(keyword);
            
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
            criteriaQuery.setPageable(pageable);
            
            SearchHits<PromoDocument> searchHits = elasticsearchOperations.search(criteriaQuery, PromoDocument.class);
            
            List<PromoDocument> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(content, pageable, searchHits.getTotalHits());
        } catch (Exception e) {
            return Page.empty(pageable);
        }
    }

    /**
     * 제목으로 검색
     */
    public List<PromoDocument> searchByTitle(String title) {
        try {
            return promoSearchRepository.findByTitleContaining(title);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 팀 이름으로 검색
     */
    public List<PromoDocument> searchByTeamName(String teamName) {
        try {
            return promoSearchRepository.findByTeamNameContaining(teamName);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 설명으로 검색
     */
    public List<PromoDocument> searchByDescription(String description) {
        try {
            return promoSearchRepository.findByDescriptionContaining(description);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 위치로 검색
     */
    public List<PromoDocument> searchByLocation(String location) {
        try {
            return promoSearchRepository.findByLocationContaining(location);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 주소로 검색
     */
    public List<PromoDocument> searchByAddress(String address) {
        try {
            return promoSearchRepository.findByAddressContaining(address);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 이벤트 날짜 범위로 검색
     */
    public List<PromoDocument> searchByEventDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return promoSearchRepository.findByEventDatetimeBetween(startDate, endDate);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 입장료 범위로 검색
     */
    public List<PromoDocument> searchByAdmissionFeeRange(Double minFee, Double maxFee) {
        try {
            return promoSearchRepository.findByAdmissionFeeBetween(minFee, maxFee);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 좋아요 수 기준으로 정렬하여 검색
     */
    public List<PromoDocument> searchByLikeCountOrder() {
        try {
            return promoSearchRepository.findByOrderByLikeCountDesc();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 생성일 기준으로 정렬하여 검색
     */
    public List<PromoDocument> searchByCreatedAtOrder() {
        try {
            return promoSearchRepository.findByOrderByCreatedAtDesc();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 날짜 범위와 팀명으로 필터링
     */
    public Page<PromoDocument> filterPromosByDateAndTeam(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            String teamName, 
            Pageable pageable) {
        try {
            Criteria criteria = new Criteria();
            
            if (startDate != null && endDate != null) {
                criteria.and("eventDatetime").between(startDate, endDate);
            } else if (startDate != null) {
                criteria.and("eventDatetime").greaterThanEqual(startDate);
            } else if (endDate != null) {
                criteria.and("eventDatetime").lessThanEqual(endDate);
            }
            
            if (teamName != null && !teamName.trim().isEmpty()) {
                criteria.and("teamName").contains(teamName);
            }
            
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
            criteriaQuery.setPageable(pageable);
            
            SearchHits<PromoDocument> searchHits = elasticsearchOperations.search(criteriaQuery, PromoDocument.class);
            
            List<PromoDocument> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(content, pageable, searchHits.getTotalHits());
        } catch (Exception e) {
            return Page.empty(pageable);
        }
    }

    /**
     * 지도 기반 검색 (위도/경도 범위)
     */
    public Page<PromoDocument> filterPromosByLocation(
            BigDecimal startLatitude, 
            BigDecimal startLongitude, 
            BigDecimal endLatitude, 
            BigDecimal endLongitude, 
            Pageable pageable) {
        try {
            // 작은 값이 start, 큰 값이 end가 되도록 정렬
            BigDecimal minLat = startLatitude.compareTo(endLatitude) < 0 ? startLatitude : endLatitude;
            BigDecimal maxLat = startLatitude.compareTo(endLatitude) > 0 ? startLatitude : endLatitude;
            BigDecimal minLng = startLongitude.compareTo(endLongitude) < 0 ? startLongitude : endLongitude;
            BigDecimal maxLng = startLongitude.compareTo(endLongitude) > 0 ? startLongitude : endLongitude;
            
            Criteria criteria = new Criteria()
                    .and("latitude").between(minLat, maxLat)
                    .and("longitude").between(minLng, maxLng);
            
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
            criteriaQuery.setPageable(pageable);
            
            SearchHits<PromoDocument> searchHits = elasticsearchOperations.search(criteriaQuery, PromoDocument.class);
            
            List<PromoDocument> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(content, pageable, searchHits.getTotalHits());
        } catch (Exception e) {
            return Page.empty(pageable);
        }
    }
} 