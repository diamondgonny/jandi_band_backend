package com.jandi.band_backend.invite.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.invite.redis.InviteType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilService {
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    /// 동아리 관련
    // 동아리 존재 확인
    protected void isExistClub(Integer clubId) {
        clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리가 존재하지 않습니다"));
    }

    // 동아리 부원인지 확인
    protected boolean isMemberOfClub(Integer clubId, Integer userId) {
        Optional<ClubMember> Optional = clubMemberRepository.findByClubIdAndUserId(clubId, userId);
        return Optional.isPresent();
    }

    // 동아리 객체 반환
    protected Club getClub(String keyId) {
        Integer clubId = getOriginalId(keyId, InviteType.CLUB);
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리가 존재하지 않습니다"));
    }

    /// 코드 관련
    private Integer getOriginalId(String keyId, InviteType inviteType) {
        String[] splitKey = keyId.split(":");
        try {
            if(splitKey.length != 2) {
                throw new IllegalArgumentException("잘못된 KeyId 형식: " + keyId);
            }
            if(!splitKey[0].equals(inviteType.name())) {
                throw new IllegalArgumentException("잘못된 초대 타입: " + splitKey[0]);
            }
            if(!splitKey[1].chars().allMatch(Character::isDigit)) {
                throw new IllegalArgumentException("잘못된 ID 형식: " + splitKey[1]);
            }
            return Integer.parseInt(splitKey[1]);
        }catch (Exception e){
            throw new IllegalArgumentException("잘못된 KeyId 형식: " + keyId);
        }
    }
}
