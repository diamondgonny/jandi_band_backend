package com.jandi.band_backend.club.service;

import com.jandi.band_backend.club.dto.ClubGalPhotoReqDTO;
import com.jandi.band_backend.club.dto.ClubGalPhotoRespDTO;
import com.jandi.band_backend.club.dto.ClubGalPhotoRespDetailDTO;
import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubGalPhoto;
import com.jandi.band_backend.club.repository.ClubGalPhotoRepository;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.image.S3Service;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ClubGalPhotoService {
    private final ClubGalPhotoRepository clubGalPhotoRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final S3Service s3Service;

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

    @Transactional(readOnly = true)
    public ClubGalPhotoRespDetailDTO getClubGalPhotoDetail(Integer clubId, Integer userId, Integer photoId) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("존재하지 않는 동아리입니다."));
        Users user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ClubGalPhoto photo = clubGalPhotoRepository.findByIdAndClubAndDeletedAtIsNull(photoId, club)
                .orElseThrow(()->new ResourceNotFoundException("존재하지 않는 사진입니다"));

        // 동아리원이라면 모든 사진 조회, 동아리원이 아니라면 공개된 것만
        boolean isMember = clubMemberRepository.existsByClubAndUserAndDeletedAtIsNull(club, user);
        boolean isPublic = photo.getIsPublic();
        if(isPublic || isMember) {
            return new ClubGalPhotoRespDetailDTO(photo);
        }else {
            throw new InvalidAccessException("권한이 없습니다: 동아리원에게만 공개된 사진입니다.");
        }
    }

    @Transactional
    public ClubGalPhotoRespDTO createClubGalPhotoList(Integer clubId, Integer userId, ClubGalPhotoReqDTO reqDTO) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("존재하지 않는 동아리입니다."));
        Users user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // 동아리원이라면 업로드 작업 수행, 동아리원이 아니라면 업로드 못하게 막기
        boolean isMember = clubMemberRepository.existsByClubAndUserAndDeletedAtIsNull(club, user);
        if(isMember) {
            return createClubPhoto(club, user, reqDTO);
        }else {
            throw new InvalidAccessException("권한이 없습니다: 동아리원이 아닙니다");
        }
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

    private ClubGalPhotoRespDTO createClubPhoto(Club club, Users user, ClubGalPhotoReqDTO reqDTO) {
        String imageUrl = uploadImage(reqDTO.getImage());

        ClubGalPhoto clubGalPhoto = new ClubGalPhoto();
        clubGalPhoto.setClub(club);
        clubGalPhoto.setUploader(user);
        clubGalPhoto.setDescription(reqDTO.getDescription());
        clubGalPhoto.setIsPublic(reqDTO.getIsPublic());
        clubGalPhoto.setImageUrl(imageUrl);

        try{
            clubGalPhotoRepository.save(clubGalPhoto);
        }catch (Exception e){
            // DB 저장 실패 시 업로드된 이미지 삭제 (롤백 처리)
            s3Service.deleteImage(imageUrl);
            throw new RuntimeException("DB 저장 실패: " + e);
        }
        return new ClubGalPhotoRespDTO(clubGalPhoto);
    }

    private String uploadImage(MultipartFile file){
        try {
            return s3Service.uploadImage(file, "club-gal-photo");
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e);
        }
    }
}
