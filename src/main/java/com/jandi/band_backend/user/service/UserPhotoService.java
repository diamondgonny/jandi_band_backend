package com.jandi.band_backend.user.service;

import com.jandi.band_backend.user.entity.UserPhoto;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserPhotoRepository;
import com.jandi.band_backend.global.util.S3FileManagementUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPhotoService {
    private final UserService userService;
    private final UserPhotoRepository userPhotoRepository;
    private final S3FileManagementUtil s3FileManagementUtil;
    
    private static final String USER_PHOTO_DIR = "user-photo";

    /// 유저 프로필 사진 조회
    @Transactional(readOnly = true)
    public UserPhoto getMyPhoto(Integer userId) {
        Users user = userService.getMyInfo(userId);
        UserPhoto userProfile = userPhotoRepository.findByUser(user);

        if (userProfile == null) {
            throw new RuntimeException("프로필 사진이 등록되지 않았습니다.");
        }
        return userProfile;
    }

    /// 유저 프로필 사진 수정
    @Transactional
    public Integer updateMyPhoto(Integer userId, MultipartFile newProfileFile) {
        // 프로필 사진이 없을 경우 수정하지 않음
        if (newProfileFile == null || newProfileFile.isEmpty()) {
            return 0;
        }

        // 프로필 조회
        UserPhoto profile = getMyPhoto(userId);
        String originalUrl = profile.getImageUrl();

        // 새 이미지 업로드 및 이전 이미지 삭제
        String newUrl = s3FileManagementUtil.uploadFile(newProfileFile, USER_PHOTO_DIR, "프로필 사진 업로드 실패");
        s3FileManagementUtil.deleteFileWithKakaoCheck(originalUrl);
        profile.setImageUrl(newUrl);
        userPhotoRepository.save(profile);
        return 1000;
    }
}
