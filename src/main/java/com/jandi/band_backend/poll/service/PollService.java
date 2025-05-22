package com.jandi.band_backend.poll.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.*;
import com.jandi.band_backend.poll.dto.*;
import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.poll.entity.PollSong;
import com.jandi.band_backend.poll.entity.Vote;
import com.jandi.band_backend.poll.entity.Vote.VotedMark;
import com.jandi.band_backend.poll.repository.PollRepository;
import com.jandi.band_backend.poll.repository.PollSongRepository;
import com.jandi.band_backend.poll.repository.VoteRepository;
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
public class PollService {

    private final PollRepository pollRepository;
    private final PollSongRepository pollSongRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public PollRespDTO createPoll(PollCreateReqDTO requestDto, Integer currentUserId) {
        // 동아리 조회
        Club club = clubRepository.findById(requestDto.getClubId())
                .orElseThrow(() -> new ClubNotFoundException("해당 동아리를 찾을 수 없습니다."));

        // 사용자 조회
        Users creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException());

        // 투표 생성
        Poll poll = new Poll();
        poll.setClub(club);
        poll.setTitle(requestDto.getTitle());
        poll.setStartDatetime(LocalDateTime.now());
        poll.setEndDatetime(requestDto.getEndDatetime());
        poll.setCreator(creator);

        Poll savedPoll = pollRepository.save(poll);

        return convertToPollRespDTO(savedPoll);
    }

    @Transactional(readOnly = true)
    public Page<PollRespDTO> getPollsByClub(Integer clubId, Pageable pageable) {
        // 동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException("해당 동아리를 찾을 수 없습니다."));

        // 동아리에 해당하는 투표 조회
        Page<Poll> polls = pollRepository.findAllByClubAndDeletedAtIsNullOrderByCreatedAtDesc(club, pageable);

        return polls.map(this::convertToPollRespDTO);
    }

    @Transactional(readOnly = true)
    public PollDetailRespDTO getPollDetail(Integer pollId) {
        // 투표 조회
        Poll poll = pollRepository.findByIdAndDeletedAtIsNull(pollId)
                .orElseThrow(() -> new PollNotFoundException("해당 투표를 찾을 수 없습니다."));

        // 투표에 해당하는 노래 목록 조회
        List<PollSong> pollSongs = pollSongRepository.findAllByPollAndDeletedAtIsNullOrderByCreatedAtDesc(poll);

        // PollSongResponseDto로 변환
        List<PollSongRespDTO> songResponseDtos = pollSongs.stream()
                .map(this::convertToPollSongRespDTO)
                .collect(Collectors.toList());

        return convertToPollDetailRespDTO(poll, songResponseDtos);
    }

    @Transactional
    public PollSongRespDTO addSongToPoll(Integer pollId, PollSongCreateReqDTO requestDto, Integer currentUserId) {
        // 투표 조회
        Poll poll = pollRepository.findByIdAndDeletedAtIsNull(pollId)
                .orElseThrow(() -> new PollNotFoundException("해당 투표를 찾을 수 없습니다."));

        // 사용자 조회
        Users suggester = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException());

        // 노래 추가
        PollSong pollSong = new PollSong();
        pollSong.setPoll(poll);
        pollSong.setSongName(requestDto.getSongName());
        pollSong.setArtistName(requestDto.getArtistName());
        pollSong.setYoutubeUrl(requestDto.getYoutubeUrl());
        pollSong.setDescription(requestDto.getDescription());
        pollSong.setSuggester(suggester);

        PollSong savedPollSong = pollSongRepository.save(pollSong);

        return convertToPollSongRespDTO(savedPollSong);
    }

    @Transactional
    public PollSongRespDTO setVoteForSong(Integer pollId, Integer songId, String voteType, Integer currentUserId) {
        // 사용자 조회
        Users user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException());

        // 노래 조회
        PollSong pollSong = pollSongRepository.findById(songId)
                .orElseThrow(() -> new PollSongNotFoundException("해당 노래를 찾을 수 없습니다."));

        // 투표 조회 (pollId 확인)
        if (!pollSong.getPoll().getId().equals(pollId)) {
            throw new PollSongNotFoundException("해당 투표에 속한 노래가 아닙니다.");
        }

        // 투표 타입 변환
        VotedMark votedMark = convertToVotedMark(voteType);

        // 사용자가 이 곡에 대한 투표를 했는지 확인 (타입 상관없이)
        List<Vote> userVotesForSong = voteRepository.findByPollSongIdAndUserId(songId, currentUserId);

        // 같은 타입의 투표가 있는지 확인
        boolean hasSameTypeVote = userVotesForSong.stream()
                .anyMatch(v -> v.getVotedMark() == votedMark);

        // 이미 같은 타입 투표가 있으면 예외 발생
        if (hasSameTypeVote) {
            throw new VoteAlreadyExistsException(
                "이미 이 노래에 대한 '" + voteType + "' 투표가 존재합니다. " +
                "취소하려면 DELETE 요청을 사용하세요."
            );
        }

        // 다른 타입의 투표가 있으면 기존 투표의 타입을 업데이트
        if (!userVotesForSong.isEmpty()) {
            // 첫 번째 투표 가져오기 (한 곡에 최대 한 개의 투표만 가능)
            Vote existingVote = userVotesForSong.getFirst();
            existingVote.setVotedMark(votedMark);
            // 추가 투표가 있으면 삭제 (혹시 모를 중복 투표 제거)
            if (userVotesForSong.size() > 1) {
                for (int i = 1; i < userVotesForSong.size(); i++) {
                    voteRepository.delete(userVotesForSong.get(i));
                }
            }
        }
        // 투표가 없으면 새로 생성
        else {
            Vote vote = new Vote();
            vote.setPollSong(pollSong);
            vote.setUser(user);
            vote.setVotedMark(votedMark);
            voteRepository.save(vote);
        }

        // 업데이트된 노래 정보 반환
        return convertToPollSongRespDTO(pollSong);
    }

    @Transactional
    public PollSongRespDTO removeVoteFromSong(Integer pollId, Integer songId, String voteType, Integer currentUserId) {
        // 노래 조회
        PollSong pollSong = pollSongRepository.findById(songId)
                .orElseThrow(() -> new PollSongNotFoundException("해당 노래를 찾을 수 없습니다."));

        // 투표 조회 (pollId 확인)
        if (!pollSong.getPoll().getId().equals(pollId)) {
            throw new PollSongNotFoundException("해당 투표에 속한 노래가 아닙니다.");
        }

        // 투표 타입 변환
        VotedMark votedMark = convertToVotedMark(voteType);

        // 사용자의 해당 타입 투표 찾기
        Vote vote = voteRepository.findByPollSongIdAndUserIdAndVotedMark(
                songId,
                currentUserId,
                votedMark
        ).orElseThrow(() -> new VoteNotFoundException(
                "사용자의 해당 노래에 대한 " + voteType + " 타입의 투표를 찾을 수 없습니다."
        ));

        // 투표 삭제
        voteRepository.delete(vote);

        // DB 조회 없이 해당 투표 타입의 카운트를 줄인 응답 생성
        return convertToPollSongRespDTOWithAdjustedVote(pollSong, voteType, -1);
    }

    private PollRespDTO convertToPollRespDTO(Poll poll) {
        return PollRespDTO.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .clubId(
                        poll.getClub() != null ?
                        poll.getClub().getId() :
                        null
                )
                .clubName(
                        poll.getClub() != null ?
                        poll.getClub().getName() :
                        null
                )
                .startDatetime(poll.getStartDatetime())
                .endDatetime(poll.getEndDatetime())
                .creatorId(
                        poll.getCreator() != null ?
                        poll.getCreator().getId() :
                        null
                )
                .creatorName(
                        poll.getCreator() != null ?
                        poll.getCreator().getNickname() :
                        null
                )
                .createdAt(poll.getCreatedAt())
                .build();
    }

    private PollDetailRespDTO convertToPollDetailRespDTO(Poll poll, List<PollSongRespDTO> songs) {
        return PollDetailRespDTO.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .clubId(
                        poll.getClub() != null ?
                        poll.getClub().getId() :
                        null
                )
                .clubName(
                        poll.getClub() != null ?
                        poll.getClub().getName() :
                        null
                )
                .startDatetime(poll.getStartDatetime())
                .endDatetime(poll.getEndDatetime())
                .creatorId(
                        poll.getCreator() != null ?
                        poll.getCreator().getId() :
                        null
                )
                .creatorName(
                        poll.getCreator() != null ?
                        poll.getCreator().getNickname() :
                        null
                )
                .createdAt(poll.getCreatedAt())
                .songs(songs)
                .build();
    }

    private PollSongRespDTO convertToPollSongRespDTO(PollSong pollSong) {
        return PollSongRespDTO.builder()
                .id(pollSong.getId())
                .pollId(
                        pollSong.getPoll() != null ?
                        pollSong.getPoll().getId() :
                        null
                )
                .songName(pollSong.getSongName())
                .artistName(pollSong.getArtistName())
                .youtubeUrl(pollSong.getYoutubeUrl())
                .description(pollSong.getDescription())
                .suggesterId(
                        pollSong.getSuggester() != null ?
                        pollSong.getSuggester().getId() :
                        null
                )
                .suggesterName(
                        pollSong.getSuggester() != null ?
                        pollSong.getSuggester().getNickname() :
                        null
                )
                .createdAt(pollSong.getCreatedAt())
                .likeCount(calculateVoteCount(pollSong, "LIKE"))
                .dislikeCount(calculateVoteCount(pollSong, "DISLIKE"))
                .cantCount(calculateVoteCount(pollSong, "CANT"))
                .hajjCount(calculateVoteCount(pollSong, "HAJJ"))
                .build();
    }

    // 특정 투표 타입의 카운트를 조정하여 DTO 반환하는 메서드 추가
    private PollSongRespDTO convertToPollSongRespDTOWithAdjustedVote(PollSong pollSong, String voteType, int adjustment) {
        // 기본 카운트 계산
        int likeCount = calculateVoteCount(pollSong, "LIKE");
        int dislikeCount = calculateVoteCount(pollSong, "DISLIKE");
        int cantCount = calculateVoteCount(pollSong, "CANT");
        int hajjCount = calculateVoteCount(pollSong, "HAJJ");

        // 해당 투표 타입의 카운트 조정
        switch (voteType.toUpperCase()) {
            case "LIKE", "좋아요" -> likeCount += adjustment;
            case "DISLIKE", "별로에요" -> dislikeCount += adjustment;
            case "CANT", "실력부족" -> cantCount += adjustment;
            case "HAJJ", "하고싶지_않은데_존중해요" -> hajjCount += adjustment;
        }

        return PollSongRespDTO.builder()
                .id(pollSong.getId())
                .pollId(
                        pollSong.getPoll() != null ?
                        pollSong.getPoll().getId() :
                        null
                )
                .songName(pollSong.getSongName())
                .artistName(pollSong.getArtistName())
                .youtubeUrl(pollSong.getYoutubeUrl())
                .description(pollSong.getDescription())
                .suggesterId(
                        pollSong.getSuggester() != null ?
                        pollSong.getSuggester().getId() :
                        null
                )
                .suggesterName(
                        pollSong.getSuggester() != null ?
                        pollSong.getSuggester().getNickname() :
                        null
                )
                .createdAt(pollSong.getCreatedAt())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .cantCount(cantCount)
                .hajjCount(hajjCount)
                .build();
    }

    private int calculateVoteCount(PollSong pollSong, String voteMark) {
        return (int) pollSong.getVotes().stream()
                .filter(vote -> vote.getVotedMark().name().equals(voteMark))
                .count();
    }

    private VotedMark convertToVotedMark(String voteType) {
        return switch (voteType.toUpperCase()) {
            case "LIKE", "좋아요" -> VotedMark.LIKE;
            case "DISLIKE", "별로에요" -> VotedMark.DISLIKE;
            case "CANT", "실력부족" -> VotedMark.CANT;
            case "HAJJ", "하고싶지_않은데_존중해요" -> VotedMark.HAJJ;
            default -> throw new IllegalArgumentException("유효하지 않은 투표 타입입니다: " + voteType);
        };
    }
}
