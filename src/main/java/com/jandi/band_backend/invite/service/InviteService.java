package com.jandi.band_backend.invite.service;

import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.invite.dto.InviteLinkRespDTO;
import com.jandi.band_backend.invite.redis.InviteCodeService;
import com.jandi.band_backend.invite.redis.InviteType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteCodeService inviteCodeService;
    private final InviteUtilService inviteUtilService;
    private static final Random RANDOM = new Random();

    @Value("${invite.club.link.prefix}") private String clubLinkPrefix;
    @Value("${invite.team.link.prefix}") private String teamLinkPrefix;

    @Transactional
    public InviteLinkRespDTO generateInviteClubLink(Integer userId, Integer clubId) {
        // 동아리 초대 권한이 있는지 검사
        inviteUtilService.isExistClub(clubId);
        if(!inviteUtilService.isMemberOfClub(userId, clubId)) {
            throw new InvalidAccessException("초대 권한이 없습니다");
        }

        // code 생성 후 Redis 서버에 저장
        String code = generateRandomCode();
        inviteCodeService.saveCode(InviteType.CLUB, clubId, code);

        // 초대 링크 생성 및 반환
        String link = clubLinkPrefix + "?code=" + code;
        return new InviteLinkRespDTO(link);
    }

    @Transactional
    public InviteLinkRespDTO generateInviteTeamLink(Integer userId, Integer teamId) {
        // 팀 초대 권한이 있는지 검사
        inviteUtilService.isExistTeam(teamId);
        if(!inviteUtilService.isMemberOfTeam(userId, teamId)) {
            throw new InvalidAccessException("초대 권한이 없습니다");
        }

        // code 생성 후 Redis 서버에 저장
        String code = generateRandomCode();
        inviteCodeService.saveCode(InviteType.TEAM, teamId, code);

        // 초대 링크 생성 및 반환
        String link = teamLinkPrefix + "?code=" + code;
        return new InviteLinkRespDTO(link);
    }

    /// 내부 메서드
    // 랜덤 코드 생성
    private String generateRandomCode() {
        return RANDOM.ints('0', 'z' + 1)
                .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
