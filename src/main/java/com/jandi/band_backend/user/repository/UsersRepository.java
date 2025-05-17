package com.jandi.band_backend.user.repository;

import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByKakaoOauthId(String kakaoOauthId);
}
