package com.jandi.band_backend.invite.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.invite.redis.InviteCodeService;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.team.entity.TeamMember;
import com.jandi.band_backend.team.repository.TeamMemberRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final InviteCodeService inviteCodeService;
    private final InviteUtilService inviteUtilService;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void joinClub(Integer userId, String code) {
        // code 유효성 검사 후 clubId 추출
        String keyId = inviteCodeService.getKeyId(code);
        Club club = inviteUtilService.getClub(keyId);

        // 유저가 이미 동아리 부원인지 검사
        if(inviteUtilService.isMemberOfClub(club.getId(), userId)) {
            throw new InvalidAccessException("이미 가입한 동아리입니다");
        }

        // 유저를 해당 동아리의 부원으로 등록
        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        createNewClubMember(user, club);
    }

    @Transactional
    public void joinTeam(Integer userId, String code) {
        // code 유효성 검사 후 clubId 추출
        String keyId = inviteCodeService.getKeyId(code);
        Team team = inviteUtilService.getTeam(keyId);

        // 유저가 이미 팀원인지 검사
        if(inviteUtilService.isMemberOfTeam(team.getId(), userId)) {
            throw new InvalidAccessException("이미 가입한 팀입니다");
        }

        // 유저를 해당 팀의 팀원으로 등록
        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        createNewTeamMember(user, team);
    }

    /// 내부 메서드
    // 동아리 부원 등록
    private void createNewClubMember(Users user, Club club) {
        ClubMember clubMember = new ClubMember();
        clubMember.setClub(club);
        clubMember.setUser(user);
        clubMemberRepository.save(clubMember);
    }

    // 팀원 등록
    private void createNewTeamMember(Users user, Team team) {
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setUser(user);
        teamMemberRepository.save(teamMember);
    }
}
