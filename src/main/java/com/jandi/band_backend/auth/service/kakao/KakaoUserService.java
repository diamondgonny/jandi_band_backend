package com.jandi.band_backend.auth.service.kakao;

import com.jandi.band_backend.auth.dto.kakao.KakaoUserInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoUserService {
    @Value("${kakao.user-info-url}")
    private String kakaoUserInfoUri;

    // 카카오 서버로부터 유저 정보 얻기
    public KakaoUserInfoDTO getKakaoUserInfo(String accessToken){
        // 액세스 토큰으로 유저 정보 요청 보내기
        Map body = requestKakaoUserInfo(accessToken);
        Map account = (Map) body.get("kakao_account");
        Map profile = (Map) account.get("profile");

        // 응답에서 유저 정보 추출
        String kakaoOauthId = String.valueOf(body.get("id"));
        String nickname = String.valueOf(profile.get("nickname"));
        String profilePhoto = String.valueOf(profile.get("profile_image_url"));

        return new KakaoUserInfoDTO(
                kakaoOauthId, nickname, profilePhoto
        );
    }

    // 카카오 토큰으로 카카오 계정 정보 요청
    private Map requestKakaoUserInfo(String accessToken){
        // 헤더에 Authorization 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // GET 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                kakaoUserInfoUri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("카카오 사용자 정보 조회 실패: " + response.getStatusCode());
        }

        Map body = response.getBody();
        if (body.get("kakao_account") == null) {
            throw new RuntimeException("카카오 사용자 계정 정보 조회 실패: " + response.getStatusCode());
        }

        Map account = (Map) body.get("kakao_account");
        if(account.get("profile") == null){
            throw new RuntimeException("카카오 사용자 프로필 정보 조회 실패: " + response.getStatusCode());
        }

        return response.getBody();
    }
}

