package com.jandi.band_backend.user.service;

import com.jandi.band_backend.user.dto.UserTimetableListRespDTO;
import com.jandi.band_backend.user.entity.UserTimetable;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserTimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTimetableService {
    private final UserService userService;
    private final UserTimetableRepository userTimetableRepository;

    /// 내 시간표 목록 조회
    public List<UserTimetableListRespDTO> getMyTimetables(String kakaoOauthId) {
        Users user = userService.getMyInfo(kakaoOauthId);

        // 내 시간표 조회 및 DTO 형태로 반환
        List<UserTimetable> myTimetables = userTimetableRepository.findByUserAndDeletedAtIsNull(user);
        return myTimetables.stream()
                .map(UserTimetableListRespDTO::new).collect(Collectors.toList());
    }
}
