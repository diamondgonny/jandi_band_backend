package com.jandi.band_backend.invite.redis;

import com.jandi.band_backend.global.exception.InvalidAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class InviteCodeService {
    private final StringRedisTemplate redisTemplate;
    @Value("${invite.expire.days}") private Integer expireDays;

    // id: 동아리 초대일 경우 id==club:{clubId}, 팀 초대일 경우 id==team:{teamId}
    public void saveCode(InviteType type, Integer id, String code) {
        String keyId = type + ":" + id;
        redisTemplate.opsForValue().set(code, keyId, Duration.ofDays(expireDays)); // 7일 후 만료
    }

    public String getKeyId(String code) {
        String keyId = redisTemplate.opsForValue().get(code);
        if(keyId == null) {
            throw new InvalidAccessException("권한 오류: code=" + code +"를 찾을 수 없습니다.");
        }
        return keyId;
    }

    public void deleteRecord(String code) {
        redisTemplate.delete(code);
    }
}
