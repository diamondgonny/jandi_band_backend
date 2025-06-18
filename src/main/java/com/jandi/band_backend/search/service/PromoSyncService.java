package com.jandi.band_backend.search.service;

import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.repository.PromoRepository;
import com.jandi.band_backend.promo.repository.PromoPhotoRepository;
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
    private final PromoPhotoRepository promoPhotoRepository;

    // 모든 Elasticsearch 동기화 메서드가 주석처리됨
    // 현재 JPA 기반 검색만 사용
} 