package com.jandi.band_backend.global.util;

import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidationUtil {
    private final UserRepository userRepository;

    /**
     * 사용자 ID로 사용자 조회 (존재하지 않으면 예외 발생)
     */
    public Users getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
} 