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
        String keyId = inviteCodeService.getKeyId(code);
        Club club = inviteUtilService.getClub(keyId);

        if(inviteUtilService.isMemberOfClub(club.getId(), userId)) {
            throw new InvalidAccessException("이미 가입한 동아리입니다");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        createNewClubMember(user, club);
    }

    @Transactional
    public void joinTeam(Integer userId, String code) {
        String keyId = inviteCodeService.getKeyId(code);
        Team team = inviteUtilService.getTeam(keyId);

        if(inviteUtilService.isMemberOfTeam(team.getId(), userId)) {
            throw new InvalidAccessException("이미 가입한 팀입니다");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        createNewTeamMember(user, team);
    }

    private void createNewClubMember(Users user, Club club) {
        ClubMember clubMember = new ClubMember();
        clubMember.setClub(club);
        clubMember.setUser(user);
        clubMemberRepository.save(clubMember);
    }

    private void createNewTeamMember(Users user, Team team) {
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setUser(user);
        teamMemberRepository.save(teamMember);
    }
}
