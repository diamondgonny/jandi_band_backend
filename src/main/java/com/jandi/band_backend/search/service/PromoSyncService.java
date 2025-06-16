package com.jandi.band_backend.search.service;

import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.repository.PromoRepository;
import com.jandi.band_backend.search.document.PromoDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromoSyncService {

    private final PromoSearchService promoSearchService;
    private final PromoRepository promoRepository;

    /**
     * 공연 홍보 생성 시 Elasticsearch에 동기화
     */
    public void syncPromoCreate(Promo promo) {
        PromoDocument promoDocument = new PromoDocument(
                promo.getId().toString(),
                promo.getTitle(),
                promo.getTeamName(),
                promo.getDescription(),
                promo.getLocation(),
                promo.getAddress(),
                promo.getLatitude(),
                promo.getLongitude(),
                promo.getAdmissionFee(),
                promo.getEventDatetime(),
                promo.getCreatedAt(),
                promo.getUpdatedAt(),
                promo.getLikeCount(),
                getImageUrl(promo)
        );
        
        promoSearchService.savePromo(promoDocument);
    }

    /**
     * 공연 홍보 업데이트 시 Elasticsearch에 동기화
     */
    public void syncPromoUpdate(Promo promo) {
        PromoDocument promoDocument = new PromoDocument(
                promo.getId().toString(),
                promo.getTitle(),
                promo.getTeamName(),
                promo.getDescription(),
                promo.getLocation(),
                promo.getAddress(),
                promo.getLatitude(),
                promo.getLongitude(),
                promo.getAdmissionFee(),
                promo.getEventDatetime(),
                promo.getCreatedAt(),
                promo.getUpdatedAt(),
                promo.getLikeCount(),
                getImageUrl(promo)
        );
        
        promoSearchService.savePromo(promoDocument);
    }

    /**
     * 공연 홍보 삭제 시 Elasticsearch에서 동기화
     */
    public void syncPromoDelete(Integer promoId) {
        promoSearchService.deletePromo(promoId.toString());
    }

    /**
     * 모든 공연 홍보 데이터를 Elasticsearch에 동기화
     */
    public void syncAllPromos() {
        try {
            log.info("전체 공연 홍보 데이터 동기화 시작");
            
            List<Promo> promos = promoRepository.findAllNotDeleted(org.springframework.data.domain.Pageable.unpaged()).getContent();
            log.info("동기화할 공연 홍보 수: {}", promos.size());
            
            int successCount = 0;
            for (Promo promo : promos) {
                try {
                    syncPromoCreate(promo);
                    successCount++;
                } catch (Exception e) {
                    log.error("공연 홍보 동기화 실패 - ID: {}, 제목: {}, 오류: {}", 
                            promo.getId(), promo.getTitle(), e.getMessage());
                }
            }
            
            log.info("전체 공연 홍보 데이터 동기화 완료 - 성공: {}, 실패: {}", 
                    successCount, promos.size() - successCount);
        } catch (Exception e) {
            log.error("전체 공연 홍보 데이터 동기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private String getImageUrl(Promo promo) {
        try {
            // 실제 구현에서는 Promo의 이미지 URL을 가져오는 로직
            return promo.getPhotos().stream()
                    .filter(photo -> photo.getDeletedAt() == null)
                    .findFirst()
                    .map(photo -> photo.getImageUrl())
                    .orElse(null);
        } catch (Exception e) {
            // 이미지 URL 가져오기 실패 시 null 반환
            return null;
        }
    }
} 