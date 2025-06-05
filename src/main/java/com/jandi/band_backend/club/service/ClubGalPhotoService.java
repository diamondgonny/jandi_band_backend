package com.jandi.band_backend.club.service;

import com.jandi.band_backend.club.dto.ClubGalPhotoReqDTO;
import com.jandi.band_backend.club.dto.ClubGalPhotoRespDTO;
import com.jandi.band_backend.club.dto.ClubGalPhotoRespDetailDTO;
import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubGalPhoto;
import com.jandi.band_backend.club.entity.ClubMember;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubGalPhotoService {
    private final ClubGalPhotoRepository clubGalPhotoRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final S3Service s3Service;

    private static final String S3_DIRNAME = "club-gal-photo";

    @Transactional(readOnly = true)
    public Page<ClubGalPhotoRespDTO> getClubGalPhotoList(Integer clubId, Integer userId, Pageable pageable) {
        return isClubMember(clubId, userId) ?
                getAllPhotoList(clubId, pageable) : getPublicPhotoList(clubId, pageable);
    }

    @Transactional(readOnly = true)
    public ClubGalPhotoRespDetailDTO getClubGalPhotoDetail(Integer clubId, Integer userId, Integer photoId) {
        ClubGalPhoto photo = getClubGalPhotoRecord(clubId, photoId);

        if(!isClubMember(clubId, userId) && !photo.getIsPublic())
            throw new InvalidAccessException("권한이 없습니다: 동아리원에게만 공개된 사진입니다.");

        return new ClubGalPhotoRespDetailDTO(photo);
    }

    @Transactional
    public ClubGalPhotoRespDTO createClubGalPhoto(Integer clubId, Integer userId, ClubGalPhotoReqDTO reqDTO) {
        if(!isClubMember(clubId, userId))
            throw new InvalidAccessException("권한이 없습니다: 동아리원만 업로드할 수 있습니다.");

        return createClubPhotoRecord(clubId, userId, reqDTO);
    }

    @Transactional
    public ClubGalPhotoRespDetailDTO updateClubGalPhoto(Integer clubId, Integer userId, Integer photoId, ClubGalPhotoReqDTO reqDTO) {
        ClubGalPhoto photo = getClubGalPhotoRecord(clubId, photoId);

        if(!isUploader(clubId, photoId, userId))
            throw new InvalidAccessException("권한이 없습니다: 본인만 수정할 수 있습니다.");

        return updateMyGalPhotoRecord(photo, reqDTO);
    }

    @Transactional
    public void deleteClubGalPhoto(Integer clubId, Integer userId, Integer photoId) {
        ClubGalPhoto photo = getClubGalPhotoRecord(clubId, photoId);

        // 업로더 혹은 동아리 대표만 삭제 가능
        if(!isUploader(clubId, photoId, userId) && !isClubRepresentative(clubId, photoId))
            throw new InvalidAccessException("권한이 없습니다: 본인 혹은 동아리 대표만 삭제할 수 있습니다.");

        deleteGalPhotoRecord(photo);
    }

    /// DB CRUD 관련
    private Page<ClubGalPhotoRespDTO> getPublicPhotoList(Integer clubId, Pageable pageable) {
        Page<ClubGalPhoto> publicPhotoPage = clubGalPhotoRepository
                .findByClubIdAndIsPublicAndDeletedAtIsNullFetchUploader(clubId, true, pageable);
        return publicPhotoPage.map(ClubGalPhotoRespDTO::new);
    }

    private Page<ClubGalPhotoRespDTO> getAllPhotoList(Integer clubId, Pageable pageable) {
        Page<ClubGalPhoto> allPhotoPage = clubGalPhotoRepository
                .findByClubIdAndDeletedAtIsNullFetchUploader(clubId, pageable);
        return allPhotoPage.map(ClubGalPhotoRespDTO::new);
    }

    private ClubGalPhoto getClubGalPhotoRecord(Integer clubId, Integer photoId) {
        Club club = getClubRecord(clubId);
        return clubGalPhotoRepository.findByIdAndClubAndDeletedAtIsNull(photoId, club)
                .orElseThrow(()->new ResourceNotFoundException("존재하지 않는 사진입니다"));
    }

    private ClubGalPhotoRespDTO createClubPhotoRecord(Integer clubId, Integer userId, ClubGalPhotoReqDTO reqDTO) {
        Club club = getClubRecord(clubId);
        Users user = getUserRecord(userId);
        String imageUrl = uploadImage(reqDTO.getImage());

        ClubGalPhoto photo = new ClubGalPhoto();
        photo.setClub(club);
        photo.setUploader(user);
        photo.setDescription(reqDTO.getDescription());
        photo.setIsPublic(reqDTO.getIsPublic());
        photo.setImageUrl(imageUrl);

        try{
            clubGalPhotoRepository.save(photo);
        }catch (Exception e){
            // DB 저장 실패 시 업로드된 이미지 삭제 (롤백 처리)
            deleteImage(imageUrl);
            throw new RuntimeException("DB 저장 실패: " + e);
        }
        return new ClubGalPhotoRespDTO(photo);
    }

    private ClubGalPhotoRespDetailDTO updateMyGalPhotoRecord(ClubGalPhoto photo, ClubGalPhotoReqDTO reqDTO) {
        String oldImageUrl = photo.getImageUrl();

        if(reqDTO.getImage() != null && !reqDTO.getImage().isEmpty()) {
            String newImageUrl = uploadImage(reqDTO.getImage());
            photo.setImageUrl(newImageUrl);
        }
        if(reqDTO.getDescription() != null) {
            photo.setDescription(reqDTO.getDescription());
        }
        if(reqDTO.getIsPublic() != null) {
            photo.setIsPublic(reqDTO.getIsPublic());
        }

        try{
            clubGalPhotoRepository.save(photo);
            deleteImage(oldImageUrl);
        }catch (Exception e){
            throw new RuntimeException("DB 저장 실패: " + e);
        }
        return new ClubGalPhotoRespDetailDTO(photo);
    }

    private void deleteGalPhotoRecord(ClubGalPhoto photo) {
        // s3 삭제
        String imageUrl = photo.getImageUrl();
        deleteImage(imageUrl);

        // soft delete 처리
        try {
            photo.setDeletedAt(LocalDateTime.now());
            clubGalPhotoRepository.save(photo);
        } catch (Exception e) {
            throw new RuntimeException("DB 삭제 실패", e);
        }
    }

    /// 권한 검증 관련
    private boolean isClubMember(Integer clubId, Integer userId) {
        Club club = getClubRecord(clubId);
        Users user = getUserRecord(userId);

        return clubMemberRepository.existsByClubAndUserAndDeletedAtIsNull(club, user);
    }

    private boolean isClubRepresentative(Integer clubId, Integer userId) {
        return clubMemberRepository.existsByUserIdAndClub_IdAndDeletedAtIsNullAndRole(
                userId, clubId, ClubMember.MemberRole.REPRESENTATIVE);
    }

    private boolean isUploader(Integer clubId, Integer photoId, Integer userId) {
        ClubGalPhoto photo = getClubGalPhotoRecord(clubId, photoId);
        Users user = getUserRecord(userId);

        return user.equals(photo.getUploader());
    }

    private Club getClubRecord(Integer clubId) {
        return clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("존재하지 않는 동아리입니다."));
    }

    private Users getUserRecord(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    /// S3 이미지 처리 관련
    private String uploadImage(MultipartFile file){
        try {
            return s3Service.uploadImage(file, S3_DIRNAME);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e);
        }
    }

    private void deleteImage(String imageUrl){
        try {
            if (imageUrl != null)
                s3Service.deleteImage(imageUrl);
        } catch (Exception e) {
            log.warn("기존 이미지 삭제 실패: {}", imageUrl, e);
        }
    }
}
