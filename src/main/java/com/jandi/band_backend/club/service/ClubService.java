package com.jandi.band_backend.club.service;

import com.jandi.band_backend.club.dto.ClubReqDTO;
import com.jandi.band_backend.club.dto.ClubDetailRespDTO;
import com.jandi.band_backend.club.dto.ClubRespDTO;
import com.jandi.band_backend.club.dto.ClubUpdateReqDTO;
import com.jandi.band_backend.club.dto.ClubMembersRespDTO;
import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.entity.ClubPhoto;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.club.repository.ClubPhotoRepository;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import com.jandi.band_backend.univ.entity.University;
import com.jandi.band_backend.univ.repository.UniversityRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.UnauthorizedClubAccessException;
import com.jandi.band_backend.global.exception.UniversityNotFoundException;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubPhotoRepository clubPhotoRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final S3Service s3Service;

    private static final String CLUB_PHOTO_DIR = "club-photo";
    private static final String DEFAULT_CLUB_PHOTO_URL = "https://jandi-rhythmeet.s3.ap-northeast-2.amazonaws.com/user-photo/31eef3b5-c915-4142-9b7f-1ff6a96a35a1.jpeg";

    @Transactional
    public ClubDetailRespDTO createClub(ClubReqDTO request, Integer userId) {
        // 사용자 확인
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 동아리 생성
        Club club = new Club();
        club.setName(request.getName());
        club.setChatroomUrl(request.getChatroomUrl());
        club.setDescription(request.getDescription());
        club.setInstagramId(request.getInstagramId());
        club.setCreatedAt(LocalDateTime.now());
        club.setUpdatedAt(LocalDateTime.now());

        // 대학 정보 설정 (연합 동아리인 경우 null)
        if (request.getUniversityId() != null) {
            String errorMessage = "대학을 찾을 수 없습니다. ID: " + request.getUniversityId();
            University university = universityRepository.findById(request.getUniversityId())
                    .orElseThrow(() -> new UniversityNotFoundException(errorMessage));
            club.setUniversity(university);
        }

        Club savedClub = clubRepository.save(club);

        // 동아리 기본 사진 설정
        ClubPhoto defaultPhoto = new ClubPhoto();
        defaultPhoto.setClub(savedClub);
        defaultPhoto.setImageUrl(DEFAULT_CLUB_PHOTO_URL);
        defaultPhoto.setIsCurrent(true);
        clubPhotoRepository.save(defaultPhoto);

        // 동아리 멤버 추가 (생성자를 대표자로 설정)
        ClubMember clubMember = new ClubMember();
        clubMember.setClub(savedClub);
        clubMember.setUser(user);
        clubMember.setRole(ClubMember.MemberRole.REPRESENTATIVE);
        clubMember.setJoinedAt(LocalDateTime.now());
        clubMember.setUpdatedAt(LocalDateTime.now());

        clubMemberRepository.save(clubMember);

        return convertToClubDetailRespDTO(savedClub, DEFAULT_CLUB_PHOTO_URL, 1);
    }

    @Transactional(readOnly = true)
    public Page<ClubRespDTO> getClubList(Pageable pageable) {
        // deletedAt이 null인 동아리만 조회하도록 수정
        Page<Club> clubPage = clubRepository.findAllByDeletedAtIsNull(pageable);

        return clubPage.map(club -> {
            // 동아리 대표 사진 URL 조회
            String photoUrl = getClubMainPhotoUrl(club.getId());

            int memberCount = club.getClubMembers().size();

            return convertToClubRespDTO(club, photoUrl, memberCount);
        });
    }

    @Transactional(readOnly = true)
    public ClubDetailRespDTO getClubDetail(Integer clubId) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        int memberCount = clubMemberRepository.countByClubId(clubId);

        // 동아리 대표 사진 URL 조회
        String photoUrl = getClubMainPhotoUrl(club.getId());

        return convertToClubDetailRespDTO(club, photoUrl, memberCount);
    }

    @Transactional(readOnly = true)
    public ClubMembersRespDTO getClubMembers(Integer clubId) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 동아리 멤버 목록 조회
        List<ClubMember> clubMembers = clubMemberRepository.findByClubId(clubId);

        // 멤버 정보 변환
        List<ClubMembersRespDTO.MemberInfoDTO> memberInfos = clubMembers.stream()
            .map(member -> this.convertToMemberInfoDTO(member))
            .toList();

        // 포지션별 카운트 계산
        Map<String, Long> positionCountMap = clubMembers.stream()
                .map(member -> member.getUser().getPosition())
                .filter(position -> position != null)
                .collect(Collectors.groupingBy(
                        position -> position.name(),
                        Collectors.counting()
                ));

        // 포지션별 카운트 매핑 (기본값 0으로 설정)
        Map<String, Integer> positionCounts = Map.of(
                "VOCAL", positionCountMap.getOrDefault("VOCAL", 0L).intValue(),
                "GUITAR", positionCountMap.getOrDefault("GUITAR", 0L).intValue(),
                "KEYBOARD", positionCountMap.getOrDefault("KEYBOARD", 0L).intValue(),
                "BASS", positionCountMap.getOrDefault("BASS", 0L).intValue(),
                "DRUM", positionCountMap.getOrDefault("DRUM", 0L).intValue()
        );

        return ClubMembersRespDTO.builder()
                .id(club.getId())
                .members(memberInfos)
                .vocalCount(positionCounts.get("VOCAL"))
                .guitarCount(positionCounts.get("GUITAR"))
                .keyboardCount(positionCounts.get("KEYBOARD"))
                .bassCount(positionCounts.get("BASS"))
                .drumCount(positionCounts.get("DRUM"))
                .totalMemberCount(clubMembers.size())
                .build();
    }

    @Transactional
    public ClubDetailRespDTO updateClub(Integer clubId, ClubUpdateReqDTO request, Integer userId) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 권한 확인 (동아리 회원이면 수정 가능)
        String updateAccessErrorMessage = "동아리 정보 수정 권한이 없습니다.";
        clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new UnauthorizedClubAccessException(updateAccessErrorMessage));

        // 동아리 정보 수정
        if (request.getName() != null) {
            club.setName(request.getName());
        }
        if (request.getChatroomUrl() != null) {
            club.setChatroomUrl(request.getChatroomUrl());
        }
        if (request.getDescription() != null) {
            club.setDescription(request.getDescription());
        }
        if (request.getInstagramId() != null) {
            club.setInstagramId(request.getInstagramId());
        }

        club.setUpdatedAt(LocalDateTime.now());

        Club updatedClub = clubRepository.save(club);
        String photoUrl = getClubMainPhotoUrl(clubId);
        int memberCount = clubMemberRepository.countByClubId(clubId);

        return convertToClubDetailRespDTO(updatedClub, photoUrl, memberCount);
    }

    @Transactional
    public void deleteClub(Integer clubId, Integer userId) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 권한 확인 (대표자만 삭제 가능)
        String deleteAccessErrorMessage = "동아리 삭제 권한이 없습니다.";
        clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .filter(member -> member.getRole() == ClubMember.MemberRole.REPRESENTATIVE)
                .orElseThrow(() -> new UnauthorizedClubAccessException(deleteAccessErrorMessage));

        // 동아리 대표 사진 S3 삭제
        deleteClubPhoto(clubId, userId);

        // 동아리 대표 사진 DB 레코드 소프트 삭제
        clubPhotoRepository
                .findByClubIdAndIsCurrentTrueAndDeletedAtIsNull(clubId)
                .ifPresent(clubPhoto -> {
                    clubPhoto.setIsCurrent(false);
                    clubPhoto.setDeletedAt(LocalDateTime.now());
                    clubPhotoRepository.save(clubPhoto);
                });

        // 동아리 삭제 (소프트 삭제)
        club.setDeletedAt(LocalDateTime.now());
        clubRepository.save(club);
    }

    @Transactional
    public String uploadClubPhoto(Integer clubId, MultipartFile image, Integer userId) throws IOException {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 권한 확인 (동아리 회원이면 업로드 가능)
        String uploadAccessErrorMessage = "동아리 사진 업로드 권한이 없습니다.";
        clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new UnauthorizedClubAccessException(uploadAccessErrorMessage));

        // 이전 사진 URL 조회
        ClubPhoto clubPhoto = clubPhotoRepository
                .findByClubIdAndIsCurrentTrueAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("이미지를 찾을 수 없습니다."));
        String originalUrl = clubPhoto.getImageUrl();

        // S3에서 이전 이미지 삭제 및 새로운 이미지 업로드 후 적용
        // 단, 이전 이미지 != 기본 이미지인 경우에만 S3에서 이전 이미지 삭제
        if (!originalUrl.equals(DEFAULT_CLUB_PHOTO_URL)) {
            deleteOriginalClubPhotoFile(originalUrl);
        }
        String newUrl = uploadNewClubPhotoFile(image);
        clubPhoto.setImageUrl(newUrl);
        clubPhoto.setUploadedAt(LocalDateTime.now());
        clubPhotoRepository.save(clubPhoto);

        return newUrl;
    }

    @Transactional
    public void deleteClubPhoto(Integer clubId, Integer userId) {
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 권한 확인 (동아리 회원이면 삭제 가능)
        String deletePhotoAccessErrorMessage = "동아리 사진 삭제 권한이 없습니다.";
        clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new UnauthorizedClubAccessException(deletePhotoAccessErrorMessage));

        // 이전 사진 URL 조회
        ClubPhoto clubPhoto = clubPhotoRepository
                .findByClubIdAndIsCurrentTrueAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("이미지를 찾을 수 없습니다."));
        String originalUrl = clubPhoto.getImageUrl();

        // S3에서 이전 이미지 삭제 및 기본 이미지 적용
        // 단, 이전 이미지 != 기본 이미지인 경우에만 S3에서 이전 이미지 삭제
        if (!originalUrl.equals(DEFAULT_CLUB_PHOTO_URL)) {
            deleteOriginalClubPhotoFile(originalUrl);
        }
        clubPhoto.setImageUrl(DEFAULT_CLUB_PHOTO_URL);
        clubPhoto.setUploadedAt(LocalDateTime.now());
        clubPhotoRepository.save(clubPhoto);
    }

    private ClubDetailRespDTO convertToClubDetailRespDTO(Club club, String photoUrl, int memberCount) {
        boolean isUnionClub = (club.getUniversity() == null);

        UniversityRespDTO universityResp = null;
        if (!isUnionClub) {
            universityResp = UniversityRespDTO.builder()
                    .id(club.getUniversity().getId())
                    .name(club.getUniversity().getName())
                    .build();
        }

        return ClubDetailRespDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .university(universityResp)
                .isUnionClub(isUnionClub)
                .chatroomUrl(club.getChatroomUrl())
                .description(club.getDescription())
                .instagramId(club.getInstagramId())
                .photoUrl(photoUrl)
                .memberCount(memberCount)
                .createdAt(club.getCreatedAt())
                .updatedAt(club.getUpdatedAt())
                .build();
    }

    private ClubRespDTO convertToClubRespDTO(Club club, String photoUrl, int memberCount) {
        // 대학 정보와 연합 동아리 여부 설정
        String universityName = null;
        boolean isUnionClub = (club.getUniversity() == null);

        if (!isUnionClub) {
            universityName = club.getUniversity().getName();
        }

        return ClubRespDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .universityName(universityName)
                .isUnionClub(isUnionClub)
                .photoUrl(photoUrl)
                .memberCount(memberCount)
                .build();
    }

    // 동아리 대표 사진 URL 조회 헬퍼 메서드
    private String getClubMainPhotoUrl(Integer clubId) {
        return clubPhotoRepository.findByClubIdAndIsCurrentTrueAndDeletedAtIsNull(clubId)
                .map(clubPhoto -> clubPhoto.getImageUrl())
                .orElse(null);
    }

    // ClubMember를 MemberInfoDTO로 변환하는 헬퍼 메서드
    private ClubMembersRespDTO.MemberInfoDTO convertToMemberInfoDTO(ClubMember member) {
        Users user = member.getUser();
        String position = user.getPosition() != null ? user.getPosition().name() : null;

        return ClubMembersRespDTO.MemberInfoDTO.builder()
                .userId(user.getId())
                .name(user.getNickname())
                .position(position)
                .build();
    }

    // S3에 새 동아리 사진 업로드하는 헬퍼 메서드
    private String uploadNewClubPhotoFile(MultipartFile newPhotoFile) {
        try{
            return s3Service.uploadImage(newPhotoFile, CLUB_PHOTO_DIR);
        }catch (Exception e) {
            throw new RuntimeException("동아리 사진 업로드 실패: " + e.getMessage());
        }
    }

    // S3에서 기존 동아리 사진 삭제하는 헬퍼 메서드
    private void deleteOriginalClubPhotoFile(String originalPhotoUrl) {
        try{
            s3Service.deleteImage(originalPhotoUrl);
        }catch (Exception e) {
            throw new RuntimeException("동아리 사진 삭제 실패: " + e.getMessage());
        }
    }
}
