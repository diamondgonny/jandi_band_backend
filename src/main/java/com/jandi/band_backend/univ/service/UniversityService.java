package com.jandi.band_backend.univ.service;

import com.jandi.band_backend.global.exception.UniversityNotFoundException;
import com.jandi.band_backend.univ.dto.UniversityDetailRespDTO;
import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import com.jandi.band_backend.univ.entity.University;
import com.jandi.band_backend.univ.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

    /// 모든 대학 리스트 조회
    @Transactional(readOnly = true)
    public List<UniversityRespDTO> getAllUniversity() {
        List<University> univList = universityRepository.findAll();
        return univList.stream()
                .map(UniversityRespDTO::new).collect(Collectors.toList());
    }

    /// 특정 대학 상세 조회
    @Transactional(readOnly = true)
    public UniversityDetailRespDTO getUniversityById(Integer id) {
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new UniversityNotFoundException("대학 정보가 없습니다"));
        return new UniversityDetailRespDTO(university);
    }
}
