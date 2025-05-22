package com.jandi.band_backend.user.service;

import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.TimetableNotFoundException;
import com.jandi.band_backend.user.dto.UserTimetableListRespDTO;
import com.jandi.band_backend.user.dto.UserTimetableReqDTO;
import com.jandi.band_backend.user.dto.UserTimetableRespDTO;
import com.jandi.band_backend.user.entity.UserTimetable;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserTimetableRepository;
import com.jandi.band_backend.user.util.UserTimetableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTimetableService {
    private final UserService userService;
    private final UserTimetableRepository userTimetableRepository;
    private final UserTimetableUtil userTimetableUtil;

    /// 내 시간표 목록 조회
    @Transactional(readOnly = true)
    public List<UserTimetableListRespDTO> getMyTimetables(String kakaoOauthId) {
        Users user = userService.getMyInfo(kakaoOauthId);

        // 내 시간표 조회 및 DTO 형태로 반환
        List<UserTimetable> myTimetables = userTimetableRepository.findByUserAndDeletedAtIsNull(user);
        return myTimetables.stream()
                .map(UserTimetableListRespDTO::new).collect(Collectors.toList());
    }

    /// 특정 시간표 조회
    public UserTimetableRespDTO getMyTimetableById(String kakaoOauthId, Integer timetableId) {
        UserTimetable myTimetable = getIfMyTimetable(kakaoOauthId, timetableId);

        return new UserTimetableRespDTO(
                myTimetable.getId(),
                myTimetable.getName(),
                userTimetableUtil.stringToJson(myTimetable.getTimetableData())
        );
    }

    /// 새 시간표 생성
    @Transactional
    public UserTimetableRespDTO createTimetable(String kakaoOauthId, UserTimetableReqDTO requestDTO) {
        Users user = userService.getMyInfo(kakaoOauthId);
        userTimetableUtil.validateTimetableRequest(requestDTO); // DTO 형식 검사

        // 새 테이블 생성
        UserTimetable newTimetable = new UserTimetable();
        newTimetable.setUser(user);
        newTimetable.setName(requestDTO.getName());
        newTimetable.setTimetableData(requestDTO.getTimetableData().toString());
        userTimetableRepository.save(newTimetable);

        // DTO로 반환
        return new UserTimetableRespDTO(
            newTimetable.getId(),
            newTimetable.getName(),
            userTimetableUtil.stringToJson(newTimetable.getTimetableData())
        );
    }

    /// 내 시간표 수정
    @Transactional
    public UserTimetableRespDTO updateTimetable(String kakaoOauthId, Integer timetableId, UserTimetableReqDTO requestDTO) {
        UserTimetable myTimetable = getIfMyTimetable(kakaoOauthId, timetableId); // 본인의 시간표일 때만 GET
        userTimetableUtil.validateTimetableRequest(requestDTO); // DTO 형식 검사

        // 시간표 수정
        myTimetable.setName(requestDTO.getName());
        myTimetable.setTimetableData(requestDTO.getTimetableData().toString());
        userTimetableRepository.save(myTimetable);

        // DTO로 반환
        return new UserTimetableRespDTO(
                myTimetable.getId(),
                myTimetable.getName(),
                userTimetableUtil.stringToJson(myTimetable.getTimetableData())
        );
    }

    /// 내 시간표 삭제
    public void deleteMyTimetable(String kakaoOauthId, Integer timetableId) {
        UserTimetable myTimetable = getIfMyTimetable(kakaoOauthId, timetableId); // 본인의 시간표일 때만 GET

        myTimetable.setDeletedAt(LocalDateTime.now());
        userTimetableRepository.save(myTimetable);
    }

    /// 내부 메서드
    // 시간표 검색 후 본인의 시간표일때만 반환
    private UserTimetable getIfMyTimetable(String kakaoOauthId, Integer timetableId) {
        Users user = userService.getMyInfo(kakaoOauthId);
        UserTimetable timetable = userTimetableRepository.findByIdAndDeletedAtIsNull(timetableId)
                .orElseThrow(TimetableNotFoundException::new);

        if(timetable.getUser() != user)
            throw new InvalidAccessException("권한이 없습니다: 본인의 시간표가 아닙니다");
        else
            return timetable;
    }
}
