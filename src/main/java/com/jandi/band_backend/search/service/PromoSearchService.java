package com.jandi.band_backend.search.service;

import com.jandi.band_backend.search.document.PromoDocument;
import com.jandi.band_backend.search.repository.PromoSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
            // 더 정교한 검색을 위해 여러 필드에 대한 쿼리 구성
            Criteria criteria = new Criteria()
                    .or("title").contains(keyword).boost(2.0f)  // 제목에 가중치 부여
                    .or("teamName").contains(keyword).boost(1.5f)  // 팀명에 가중치 부여
                    .or("description").contains(keyword)
                    .or("location").contains(keyword)
                    .or("address").contains(keyword);
            
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
            criteriaQuery.setPageable(pageable);
            
            SearchHits<PromoDocument> searchHits = elasticsearchOperations.search(criteriaQuery, PromoDocument.class);
            
            List<PromoDocument> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(content, pageable, searchHits.getTotalHits());
        } catch (Exception e) {
            log.error("검색 중 오류 발생 - 키워드: {}, 오류: {}", keyword, e.getMessage(), e);
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
    public List<PromoDocument> searchByEventDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return promoSearchRepository.findByEventDateBetween(startDate, endDate);
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
            LocalDate startDate, 
            LocalDate endDate, 
            String teamName, 
            Pageable pageable) {
        try {
            Criteria criteria = new Criteria();
            
            if (startDate != null && endDate != null) {
                criteria.and("eventDate").between(startDate, endDate);
            } else if (startDate != null) {
                criteria.and("eventDate").greaterThanEqual(startDate);
            } else if (endDate != null) {
                criteria.and("eventDate").lessThanEqual(endDate);
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

    /**
     * 공연 상태별 필터링 (진행 중, 예정, 종료)
     * @param status "ongoing" (진행 중), "upcoming" (예정), "ended" (종료)
     */
    public Page<PromoDocument> filterPromosByStatus(String status, Pageable pageable) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            
            // 직접 Elasticsearch 쿼리 실행
            String query;
            switch (status.toLowerCase()) {
                case "ongoing":
                    // 진행 중인 공연: 오늘 날짜와 일치하는 공연
                    query = String.format("""
                        {
                          "term": {
                            "eventDate": "%s"
                          }
                        }
                        """, today);
                    break;
                case "upcoming":
                    // 예정된 공연: 오늘 이후의 공연 (오늘 날짜보다 큰 공연)
                    query = String.format("""
                        {
                          "range": {
                            "eventDate": {
                              "gt": "%s"
                            }
                          }
                        }
                        """, today);
                    break;
                case "ended":
                    // 종료된 공연: 오늘 이전의 공연 (오늘 날짜보다 작은 공연)
                    query = String.format("""
                        {
                          "range": {
                            "eventDate": {
                              "lt": "%s"
                            }
                          }
                        }
                        """, today);
                    break;
                default:
                    // 잘못된 상태값이면 빈 결과 반환
                    return Page.empty(pageable);
            }
            
            // Elasticsearch 직접 쿼리 실행
            SearchHits<PromoDocument> searchHits = elasticsearchOperations.search(
                new org.springframework.data.elasticsearch.core.query.StringQuery(query), 
                PromoDocument.class
            );
            
            List<PromoDocument> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            // 페이징 처리
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), content.size());
            List<PromoDocument> pagedContent = content.subList(start, end);
            
            return new PageImpl<>(pagedContent, pageable, content.size());
        } catch (Exception e) {
            log.error("공연 상태별 필터링 중 오류 발생 - 상태: {}, 오류: {}", status, e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    /**
     * 공연 상태별 필터링 + 추가 조건 (키워드, 팀명 등)
     * @param status "ongoing" (진행 중), "upcoming" (예정), "ended" (종료)
     * @param keyword 검색 키워드 (선택사항)
     * @param teamName 팀명 (선택사항)
     */
    public Page<PromoDocument> filterPromosByStatusWithConditions(
            String status, 
            String keyword, 
            String teamName, 
            Pageable pageable) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            Criteria criteria = new Criteria();
            
            // 공연 상태 조건
            switch (status.toLowerCase()) {
                case "ongoing":
                    criteria.and("eventDate").is(today);
                    break;
                case "upcoming":
                    criteria.and("eventDate").greaterThan(today);
                    break;
                case "ended":
                    criteria.and("eventDate").lessThan(today);
                    break;
                default:
                    return Page.empty(pageable);
            }
            
            // 키워드 검색 조건 (제목, 설명, 장소, 주소)
            if (keyword != null && !keyword.trim().isEmpty()) {
                Criteria keywordCriteria = new Criteria()
                        .or("title").contains(keyword).boost(2.0f)
                        .or("teamName").contains(keyword).boost(1.5f)
                        .or("description").contains(keyword)
                        .or("location").contains(keyword)
                        .or("address").contains(keyword);
                criteria.and(keywordCriteria);
            }
            
            // 팀명 조건
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
            log.error("공연 상태별 필터링 중 오류 발생 - 상태: {}, 키워드: {}, 팀명: {}, 오류: {}", 
                    status, keyword, teamName, e.getMessage(), e);
            return Page.empty(pageable);
        }
    }
} 