package com.jandi.band_backend.search.service;

import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.repository.PromoRepository;
import com.jandi.band_backend.search.document.PromoDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<Promo> promos = promoRepository.findAllNotDeleted(org.springframework.data.domain.Pageable.unpaged()).getContent();
        promos.forEach(this::syncPromoCreate);
    }

    private String getImageUrl(Promo promo) {
        // 실제 구현에서는 Promo의 이미지 URL을 가져오는 로직
        return promo.getPhotos().stream()
                .filter(photo -> photo.getDeletedAt() == null)
                .findFirst()
                .map(photo -> photo.getImageUrl())
                .orElse(null);
    }
} 