package com.jandi.band_backend.user.service;

import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.user.entity.UserPhoto;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserPhotoRepository;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;

    /// 내 정보 조회
    public UserInfoDTO getMyInfo(String kakaoOauthId) {
        // 유저 및 유저 프로필 조회
        Users user = userRepository.findByKakaoOauthId(kakaoOauthId)
                .orElseThrow(UserNotFoundException::new);
        UserPhoto userProfile = userPhotoRepository.findByUser(user);

        return new UserInfoDTO(user, userProfile);
    }
}
