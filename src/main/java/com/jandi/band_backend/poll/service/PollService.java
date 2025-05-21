package com.jandi.band_backend.poll.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.PollNotFoundException;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.poll.dto.*;
import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.poll.entity.PollSong;
import com.jandi.band_backend.poll.repository.PollRepository;
import com.jandi.band_backend.poll.repository.PollSongRepository;
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
        poll.setStartDatetime(LocalDateTime.now()); // 현재 시간 저장
        poll.setEndDatetime(requestDto.getEndDatetime()); // 클라이언트에서 전달한 시간 저장
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

    private PollRespDTO convertToPollRespDTO(Poll poll) {
        return PollRespDTO.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .clubId(poll.getClub() != null ? poll.getClub().getId() : null)
                .clubName(poll.getClub() != null ? poll.getClub().getName() : null)
                .startDatetime(poll.getStartDatetime())
                .endDatetime(poll.getEndDatetime())
                .creatorId(poll.getCreator() != null ? poll.getCreator().getId() : null)
                .creatorName(poll.getCreator() != null ? poll.getCreator().getNickname() : null)
                .createdAt(poll.getCreatedAt())
                .build();
    }

    private PollDetailRespDTO convertToPollDetailRespDTO(Poll poll, List<PollSongRespDTO> songs) {
        return PollDetailRespDTO.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .clubId(poll.getClub() != null ? poll.getClub().getId() : null)
                .clubName(poll.getClub() != null ? poll.getClub().getName() : null)
                .startDatetime(poll.getStartDatetime())
                .endDatetime(poll.getEndDatetime())
                .creatorId(poll.getCreator() != null ? poll.getCreator().getId() : null)
                .creatorName(poll.getCreator() != null ? poll.getCreator().getNickname() : null)
                .createdAt(poll.getCreatedAt())
                .songs(songs)
                .build();
    }

    private PollSongRespDTO convertToPollSongRespDTO(PollSong pollSong) {
        return PollSongRespDTO.builder()
                .id(pollSong.getId())
                .pollId(pollSong.getPoll() != null ? pollSong.getPoll().getId() : null)
                .songName(pollSong.getSongName())
                .artistName(pollSong.getArtistName())
                .youtubeUrl(pollSong.getYoutubeUrl())
                .description(pollSong.getDescription())
                .suggesterId(pollSong.getSuggester() != null ? pollSong.getSuggester().getId() : null)
                .suggesterName(pollSong.getSuggester() != null ? pollSong.getSuggester().getNickname() : null)
                .createdAt(pollSong.getCreatedAt())
                .likeCount(calculateVoteCount(pollSong, "LIKE"))
                .dislikeCount(calculateVoteCount(pollSong, "DISLIKE"))
                .cantCount(calculateVoteCount(pollSong, "CANT"))
                .hajjCount(calculateVoteCount(pollSong, "HAJJ"))
                .build();
    }

    private int calculateVoteCount(PollSong pollSong, String voteMark) {
        return (int) pollSong.getVotes().stream()
                .filter(vote -> vote.getVotedMark().name().equals(voteMark))
                .count();
    }
}
