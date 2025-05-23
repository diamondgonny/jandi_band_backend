package com.jandi.band_backend.team.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.UnauthorizedClubAccessException;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.team.dto.TeamDetailRespDTO;
import com.jandi.band_backend.team.dto.TeamReqDTO;
import com.jandi.band_backend.team.dto.TeamRespDTO;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.team.entity.TeamMember;
import com.jandi.band_backend.team.repository.TeamMemberRepository;
import com.jandi.band_backend.team.repository.TeamRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;

    /**
     * 곡 팀 생성
     */
    @Transactional
    public TeamDetailRespDTO createTeam(Integer clubId, TeamReqDTO teamReqDTO, Integer currentUserId) {
        // 동아리 존재 확인
        Club club = clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 요청 사용자 확인
        Users currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 동아리 부원 권한 확인
        ClubMember clubMember = clubMemberRepository.findByClubIdAndUserId(clubId, currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("동아리 부원만 팀을 생성할 수 있습니다."));

        // 팀 생성
        Team team = new Team();
        team.setClub(club);
        team.setName(teamReqDTO.getName());
        team.setCreator(currentUser);

        Team savedTeam = teamRepository.save(team);

        // 생성자를 팀원으로 추가
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(savedTeam);
        teamMember.setUser(currentUser);
        teamMemberRepository.save(teamMember);

        // 팀 멤버 목록 조회
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(savedTeam.getId());

        return createTeamDetailRespDTO(savedTeam, teamMembers);
    }

    /**
     * 동아리별 팀 목록 조회
     */
    public Page<TeamRespDTO> getTeamsByClub(Integer clubId, Pageable pageable, Integer currentUserId) {
        // 동아리 존재 확인
        clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        // 동아리 부원 권한 확인
        clubMemberRepository.findByClubIdAndUserId(clubId, currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("동아리 부원만 팀 목록을 조회할 수 있습니다."));

        Page<Team> teams = teamRepository.findAllByClubId(clubId, pageable);

        return teams.map(team -> {
            // N+1 문제 해결: size() 대신 countByTeamId 사용
            Integer memberCount = teamMemberRepository.countByTeamId(team.getId());
            return createTeamRespDTO(team, memberCount);
        });
    }

    /**
     * 팀 상세 조회
     */
    public TeamDetailRespDTO getTeamDetail(Integer teamId, Integer currentUserId) {
        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 동아리 부원 권한 확인
        clubMemberRepository.findByClubIdAndUserId(team.getClub().getId(), currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("해당 동아리 부원만 팀 정보를 조회할 수 있습니다."));

        // 팀 멤버 목록 조회
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(teamId);

        return createTeamDetailRespDTO(team, teamMembers);
    }

    /**
     * 팀 정보 수정
     */
    @Transactional
    public TeamDetailRespDTO updateTeam(Integer teamId, TeamReqDTO teamReqDTO, Integer currentUserId) {
        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 권한 확인 (팀 생성자 또는 동아리 대표자)
        validateTeamModificationPermission(team, currentUserId);

        // 팀 정보 수정
        team.setName(teamReqDTO.getName());

        Team updatedTeam = teamRepository.save(team);

        // 팀 멤버 목록 조회
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(teamId);

        return createTeamDetailRespDTO(updatedTeam, teamMembers);
    }

    /**
     * 팀 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteTeam(Integer teamId, Integer currentUserId) {
        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 권한 확인 (팀 생성자 또는 동아리 대표자)
        validateTeamModificationPermission(team, currentUserId);

        // 소프트 삭제
        team.setDeletedAt(LocalDateTime.now());
        teamRepository.save(team);
    }

    /**
     * 팀 수정/삭제 권한 확인 (팀 생성자 또는 동아리 대표자)
     */
    private void validateTeamModificationPermission(Team team, Integer currentUserId) {
        boolean isCreator = team.getCreator().getId().equals(currentUserId);
        boolean isRepresentative = false;

        ClubMember clubMember = clubMemberRepository.findByClubIdAndUserId(team.getClub().getId(), currentUserId)
                .orElse(null);

        if (clubMember != null && clubMember.getRole() == ClubMember.MemberRole.REPRESENTATIVE) {
            isRepresentative = true;
        }

        if (!isCreator && !isRepresentative) {
            throw new UnauthorizedClubAccessException("팀 생성자 또는 동아리 대표자만 팀 정보를 수정할 수 있습니다.");
        }
    }

    /**
     * TeamRespDTO 생성
     */
    private TeamRespDTO createTeamRespDTO(Team team, Integer memberCount) {
        return TeamRespDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .creatorId(team.getCreator().getId())
                .creatorName(team.getCreator().getNickname())
                .memberCount(memberCount)
                .createdAt(team.getCreatedAt())
                .build();
    }

    /**
     * TeamDetailRespDTO 생성
     */
    private TeamDetailRespDTO createTeamDetailRespDTO(Team team, List<TeamMember> teamMembers) {
        return TeamDetailRespDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .club(createClubInfoDTO(team.getClub()))
                .creator(createCreatorInfoDTO(team.getCreator()))
                .members(teamMembers.stream()
                        .map(this::createMemberInfoDTO)
                        .collect(Collectors.toList()))
                .memberCount(teamMembers.size())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    /**
     * ClubInfoDTO 생성
     */
    private TeamDetailRespDTO.ClubInfoDTO createClubInfoDTO(Club club) {
        return TeamDetailRespDTO.ClubInfoDTO.builder()
                .clubId(club.getId())
                .name(club.getName())
                .build();
    }

    /**
     * CreatorInfoDTO 생성
     */
    private TeamDetailRespDTO.CreatorInfoDTO createCreatorInfoDTO(Users user) {
        return TeamDetailRespDTO.CreatorInfoDTO.builder()
                .userId(user.getId())
                .name(user.getNickname())
                .build();
    }

    /**
     * MemberInfoDTO 생성
     */
    private TeamDetailRespDTO.MemberInfoDTO createMemberInfoDTO(TeamMember teamMember) {
        return TeamDetailRespDTO.MemberInfoDTO.builder()
                .userId(teamMember.getUser().getId())
                .name(teamMember.getUser().getNickname())
                .position(teamMember.getUser().getPosition() != null ?
                        teamMember.getUser().getPosition().name() : null)
                .build();
    }
}
