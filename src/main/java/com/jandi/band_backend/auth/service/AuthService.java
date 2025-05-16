package com.jandi.band_backend.auth.service;

import com.jandi.band_backend.auth.dto.*;
import com.jandi.band_backend.auth.dto.kakao.KakaoTokenRespDTO;
import com.jandi.band_backend.auth.dto.kakao.KakaoUserInfoDTO;
import com.jandi.band_backend.auth.service.kakao.KaKaoTokenService;
import com.jandi.band_backend.auth.service.kakao.KakaoUserService;
import com.jandi.band_backend.image.entity.Photo;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import com.jandi.band_backend.univ.entity.University;
import com.jandi.band_backend.univ.repository.UniversityRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final KaKaoTokenService kaKaoTokenService;
    private final KakaoUserService kakaoUserService;
    private final UsersRepository usersRepository;
    private final UniversityRepository universityRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /// 로그인
    public AuthRespDTO login(String code){
        KakaoTokenRespDTO kakaoToken = kaKaoTokenService.getKakaoToken(code);
        KakaoUserInfoDTO kakaoUserInfo = kakaoUserService.getKakaoUserInfo(kakaoToken.getAccessToken());
        String kakaoOauthId = kakaoUserInfo.getKakaoOauthId();

        // DB에서 유저 정보 찾기 -> 없다면 임시 회원 정보 생성
        Optional<Users> user = usersRepository.findByKakaoOauthId(kakaoOauthId);
        if(user.isEmpty())
            signup(kakaoUserInfo);

        // 자체 jwt 토큰 발급
        return new AuthRespDTO(
            jwtTokenProvider.generateAccessToken(kakaoOauthId),
            jwtTokenProvider.generateRefreshToken(kakaoOauthId)
        );
    }

    // 정식 회원가입
    public UserInfoDTO signup(String kakaoOauthId, SignUpReqDTO reqDTO) {
        log.info("정식 회원가입");

        // 유저 조회
        Users user = usersRepository.findByKakaoOauthId(kakaoOauthId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // reqDTO 검증
        University university = universityRepository.findByName(reqDTO.getUniversity())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대학입니다."));
        Users.Position position = Users.Position.valueOf(reqDTO.getPosition());

        // 기본 유저 정보 입력
        user.setUniversity(university);
        user.setPosition(position);

        usersRepository.save(user);
        return new UserInfoDTO(user);
    }

    // 임시 회원 가입
    private void signup(KakaoUserInfoDTO kakaoUserInfo){
        Users newUser = new Users();

        newUser.setKakaoOauthId(kakaoUserInfo.getKakaoOauthId());
        newUser.setNickname(kakaoUserInfo.getNickname());
        newUser.setProfilePhoto(null); // 카카오 프로필 사진을 넣어야 하는데 DB 수정될 거라 일단 보류

        usersRepository.save(newUser);
    }
}
