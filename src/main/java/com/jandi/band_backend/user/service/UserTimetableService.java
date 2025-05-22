package com.jandi.band_backend.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.TimetableNotFoundException;
import com.jandi.band_backend.user.dto.UserTimetableListRespDTO;
import com.jandi.band_backend.user.dto.UserTimetableReqDTO;
import com.jandi.band_backend.user.dto.UserTimetableRespDTO;
import com.jandi.band_backend.user.entity.UserTimetable;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserTimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTimetableService {
    private final UserService userService;
    private final UserTimetableRepository userTimetableRepository;
    private static final Set<String> WEEKDAYS = Set.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

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
        UserTimetable myTimetable = userTimetableRepository.findByIdAndDeletedAtIsNull(timetableId)
                .orElseThrow(TimetableNotFoundException::new);

        // 본인의 시간표인지 검사
        Users user = userService.getMyInfo(kakaoOauthId);
        if(myTimetable.getUser() != user)
            throw new InvalidAccessException("본인의 시간표만 열람할 수 있습니다");

        return new UserTimetableRespDTO(
                myTimetable.getId(),
                myTimetable.getName(),
                stringToJson(myTimetable.getTimetableData())
        );
    }

    /// 새 시간표 생성
    @Transactional
    public UserTimetableRespDTO createTimetable(String kakaoOauthId, UserTimetableReqDTO requestDTO) {
        Users user = userService.getMyInfo(kakaoOauthId);

        // DTO 검증
        String name = requestDTO.getName();
        JsonNode timetableData = requestDTO.getTimetableData();
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("제목은 공란이 될 수 없습니다.");
        else if(timetableData == null || timetableData.isEmpty())
            throw new IllegalArgumentException("시간표 데이터는 공란이 될 수 없습니다");

        // 시간표 형식 검증
        checkWeekFormIsValid(timetableData);
        checkTimeListFormIsValid(timetableData);

        // 새 테이블 생성
        UserTimetable newTimetable = new UserTimetable();
        newTimetable.setUser(user);
        newTimetable.setName(name);
        newTimetable.setTimetableData(timetableData.toString());
        userTimetableRepository.save(newTimetable);

        // DTO로 반환
        return new UserTimetableRespDTO(
            newTimetable.getId(),
            newTimetable.getName(),
            stringToJson(newTimetable.getTimetableData())
        );
    }

    /// 내 시간표 수정
    @Transactional
    public UserTimetableRespDTO updateTimetable(String kakaoOauthId, Integer timetableId, UserTimetableReqDTO requestDTO) {
        UserTimetable myTimetable = userTimetableRepository.findByIdAndDeletedAtIsNull(timetableId)
                .orElseThrow(TimetableNotFoundException::new);

        // 본인의 시간표인지 검사
        Users user = userService.getMyInfo(kakaoOauthId);
        if(myTimetable.getUser() != user)
            throw new InvalidAccessException("본인의 시간표만 열람할 수 있습니다");

        // DTO 검증
        String name = requestDTO.getName();
        JsonNode timetableData = requestDTO.getTimetableData();
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("제목은 공란이 될 수 없습니다.");
        else if(timetableData == null || timetableData.isEmpty())
            throw new IllegalArgumentException("시간표 데이터는 공란이 될 수 없습니다");

        // 시간표 형식 검증
        checkWeekFormIsValid(timetableData);
        checkTimeListFormIsValid(timetableData);

        // 시간표 수정
        myTimetable.setName(name);
        myTimetable.setTimetableData(timetableData.toString());
        userTimetableRepository.save(myTimetable);

        // DTO로 반환
        return new UserTimetableRespDTO(
                myTimetable.getId(),
                myTimetable.getName(),
                stringToJson(myTimetable.getTimetableData())
        );
    }

    /// 내부 메서드
    // String 형식의 시간표 데이터를 JSON으로 변환
    private JsonNode stringToJson(String stringData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(stringData);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환에 실패했습니다");
        }
    }

    // JSON에 MON~SUN만 있는지 검사
    private void checkWeekFormIsValid(JsonNode jsonNode){
        // 모든 요일이 있는지 확인
        for (String curWeek : WEEKDAYS) {
            if(!jsonNode.has(curWeek))
                throw new IllegalArgumentException("시간표는 모든 요일을 포함해야 합니다: " + curWeek + " 누락되었습니다.");
        }

        // 불필요한 요일이 있는지 확인
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            if (!WEEKDAYS.contains(field)) {
                throw new IllegalArgumentException("시간표에 잘못된 요일 키가 존재합니다: " + field);
            }
        }
    }

    // 각 요일마다 중복 없이 시간 리스트가 들어가있는지 검사
    private void checkTimeListFormIsValid(JsonNode jsonNode){
        for (String curWeek : WEEKDAYS) {
            // 특정 요일의 시간 데이터가 배열 형식인지 확인
            JsonNode timeListNode = jsonNode.get(curWeek);
            if (!timeListNode.isArray())
                throw new IllegalArgumentException(curWeek + "의 시간 정보가 배열이 아닙니다.");

            // 특정 요일에 중복된 시간 데이터가 있는지 확인
            Set<String> seenTimes = new HashSet<>();
            for (JsonNode timeNode : timeListNode) {
                String timeStr = checkTimeFormIsValid(timeNode);// 시간 형식이 올바른지 검사
                if (!seenTimes.add(timeStr)) {
                    throw new IllegalArgumentException(curWeek + "에 중복된 시간 " + timeNode.asText() + "이 존재합니다.");
                }
            }
        }
    }

    // 시간 형식이 HH:MM(MM==00 or 30)을 지켰는지 검사
    private String checkTimeFormIsValid(JsonNode timeNode){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // 시간 형식이 HH:MM인지 확인
        LocalTime convertedTime;
        try {
            convertedTime = LocalTime.parse(timeNode.asText(), timeFormatter);
        }catch (Exception e){
            throw new IllegalArgumentException(timeNode.asText() + "은 HH:mm (HH: 00~24, mm: 00, 30)형식의 문자열이어야 합니다.");
        }

        // MM이 30분 단위(00 or 30)인지 확인
        int minute = convertedTime.getMinute();
        if (minute != 0 && minute != 30) {
            throw new IllegalArgumentException(timeNode.asText() + "은 30분 단위여야 합니다.");
        }
        return timeNode.asText();
    }
}
