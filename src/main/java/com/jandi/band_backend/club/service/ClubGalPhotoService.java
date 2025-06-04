package com.jandi.band_backend.club.service;

import com.jandi.band_backend.club.dto.ClubGalPhotoRespDTO;
import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubGalPhoto;
import com.jandi.band_backend.club.repository.ClubGalPhotoRepository;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubGalPhotoService {
    private final ClubGalPhotoRepository clubGalPhotoRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Transactional(readOnly = true)
    public Page<ClubGalPhotoRespDTO> getClubGalPhotoList(Integer clubId, Integer userId, Pageable pageable) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("존재하지 않는 동아리입니다."));
        Users user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // 동아리원이라면 모든 사진 조회, 동아리원이 아니라면 공개된 것만
        boolean isMember = clubMemberRepository.existsByClubAndUserAndDeletedAtIsNull(club, user);
        return isMember ?
                getAllPhoto(clubId, pageable) : getPublicPhoto(clubId, pageable);
    }


    /// 내부 메서드
    private Page<ClubGalPhotoRespDTO> getPublicPhoto(Integer clubId, Pageable pageable) {
        Page<ClubGalPhoto> publicPhotoPage = clubGalPhotoRepository.findByClubIdAndIsPublicAndDeletedAtIsNullFetchUploader(clubId, true, pageable);
        return publicPhotoPage.map(ClubGalPhotoRespDTO::new);
    }

    private Page<ClubGalPhotoRespDTO> getAllPhoto(Integer clubId, Pageable pageable) {
        Page<ClubGalPhoto> allPhotoPage = clubGalPhotoRepository.findByClubIdAndDeletedAtIsNullFetchUploader(clubId, pageable);
        return allPhotoPage.map(ClubGalPhotoRespDTO::new);
    }
}
