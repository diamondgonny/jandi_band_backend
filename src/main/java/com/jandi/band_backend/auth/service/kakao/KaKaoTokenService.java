package com.jandi.band_backend.auth.service.kakao;

import com.jandi.band_backend.auth.dto.kakao.KakaoTokenRespDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KaKaoTokenService {
    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUri;

    @Value("${kakao.token-url}")
    private String kakaoTokenUri;

    // 카카오 서버로부터 토큰 얻기
    public KakaoTokenRespDTO getKakaoToken(String code){
        // 인가 코드로 토큰 발급 요청 보내기
        Map body = requestKakaoToken(code);

        // 응답에서 토큰 추출
        String accessToken = (String) body.get("access_token");
        String refreshToken = (String) body.get("refresh_token");
        Integer expiresIn = (Integer) body.get("expires_in");

        return new KakaoTokenRespDTO(
                accessToken,
                refreshToken,
                (expiresIn != null) ? expiresIn : 0
        );
    }

    // 인가 코드로 토큰 발급 요청
    private Map requestKakaoToken(String code){
        // 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        // 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                kakaoTokenUri,
                new HttpEntity<>(params, headers),
                Map.class
        );

        // 잘못된 응답 처리
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            throw new RuntimeException("카카오 토큰 발급 실패: " + response.getStatusCode());
        }
        return response.getBody();
    }
}
